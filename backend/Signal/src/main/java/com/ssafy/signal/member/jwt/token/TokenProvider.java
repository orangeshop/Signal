package com.ssafy.signal.member.jwt.token;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.domain.TokenBlacklist;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.jwt.token.dto.TokenValidationResult;
import com.ssafy.signal.member.principle.UserPrinciple;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_ID_KEY = "tokenId";
    private static final String USERNAME_KEY = "username";

    private final Key hashKey;
    private final long accessTokenValidationInMilliseconds;
    private final long refreshTokenValidationInMilliseconds;

    private final MemberRepository memberRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public TokenProvider(String secrete, long accessTokenValidationInSeconds, MemberRepository memberRepository, TokenBlacklistService tokenBlacklistService) {
        this.memberRepository = memberRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secrete);
        this.hashKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidationInMilliseconds = accessTokenValidationInSeconds + 1000 * 60 * 60 * 10;
        this.refreshTokenValidationInMilliseconds = accessTokenValidationInSeconds + 1000 * 60 * 60 * 24 * 3;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public TokenInfo createToken(Member member) {
        long currentTime = (new Date()).getTime();
        Date accessTokenExpirationTime = new Date(currentTime + this.accessTokenValidationInMilliseconds);
        String tokenId = UUID.randomUUID().toString();

        String accessToken = Jwts.builder()
                .setSubject(member.getLoginId())
                .claim(USERNAME_KEY, member.getName())
                .claim(AUTHORITIES_KEY, member.getType())
                .claim(TOKEN_ID_KEY, tokenId)
                .signWith(hashKey, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenExpirationTime)
                .compact();

        Date refreshTokenExpirationTime = new Date(currentTime + this.refreshTokenValidationInMilliseconds);
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = Jwts.builder()
                .setSubject(member.getLoginId())
                .claim(TOKEN_ID_KEY, refreshTokenId)
                .signWith(hashKey, SignatureAlgorithm.HS512)
                .setExpiration(refreshTokenExpirationTime)
                .compact();


        return TokenInfo.builder()
                .status(true)
                .member(member)
                .tokenId(tokenId)
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpirationTime)
                .refreshToken(refreshToken)
                .refreshTokenExpireTime(refreshTokenExpirationTime)
                .build();
    }

    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(hashKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.debug("Claims: {}", claims);
            return new TokenValidationResult(TokenStatus.TOKEN_VALID, TokenType.ACCESS, claims.get(TOKEN_ID_KEY, String.class), claims);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰");
            return getExpiredTokenValidationResult(e);
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명");
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 서명");
            return new TokenValidationResult(TokenStatus.TOKEN_HASH_NOT_SUPPORTED, null, null, null);
        } catch (IllegalArgumentException e) {
            log.info("잘못된 JWT 토큰");
            return new TokenValidationResult(TokenStatus.TOKEN_WRONG_SIGNATURE, null, null, null);
        }
    }

    private static TokenValidationResult getExpiredTokenValidationResult(ExpiredJwtException e) {
        Claims claims = e.getClaims();
        return new TokenValidationResult(TokenStatus.TOKEN_EXPIRED, TokenType.ACCESS, claims.get(TOKEN_ID_KEY, String.class), null);
    }

    public Authentication getAuthentication(String token, Claims claims) {
        log.debug("Claims: {}", claims);
        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
        if (authoritiesClaim == null) {
            throw new IllegalArgumentException("Claims does not contain authorities key");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserPrinciple principle = new UserPrinciple(claims.getSubject(), claims.get(USERNAME_KEY, String.class), authorities);

        return new UsernamePasswordAuthenticationToken(principle, token, authorities);
    }

    public String resolveToken(String token) {
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            throw new IllegalArgumentException("Refresh token이 블랙리스트에 포함되어 있습니다.");
        }
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }


    public Date getExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(hashKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    public TokenInfo refreshToken(String refreshToken) {
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            return TokenInfo.builder()
                    .status(null)
                    .member(null)
                    .tokenId(null)
                    .accessToken(null)
                    .accessTokenExpireTime(null)
                    .refreshToken(null)
                    .refreshTokenExpireTime(null)
                    .build();
        } else {
            try {
                refreshToken = refreshToken.substring(7);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(hashKey)
                        .build()
                        .parseClaimsJws(refreshToken)
                        .getBody();

                String loginId = claims.getSubject();
                Member member = memberRepository.findByLoginId(loginId)
                        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

                Instant expirationInstant = getExpiration(refreshToken).toInstant();
                LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationInstant, ZoneId.systemDefault());
                tokenBlacklistService.blacklistToken(refreshToken, expirationTime);

                return createToken(member);
            } catch (ExpiredJwtException e) {
                throw new IllegalArgumentException("Refresh token이 만료되었습니다.");
            } catch (JwtException | IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 refresh token입니다.");
            }
        }
        }
}
