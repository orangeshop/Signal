package com.ssafy.signal.member.jwt.token;

import com.ssafy.signal.member.domain.Member;
import com.ssafy.signal.member.jwt.token.dto.TokenInfo;
import com.ssafy.signal.member.jwt.token.dto.TokenValidationResult;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_ID_KEY = "tokenId";
    private static final String USERNAME_KEY = "username";

    private final Key hashKey;
    private final long accessTokenValidationInMilliseconds;

    public TokenProvider(String secrete, long accessTokenValidationInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secrete);
        this.hashKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidationInMilliseconds = accessTokenValidationInSeconds * 1000;
    }

    public TokenInfo createToken(Member member) {
        Long currentTime = (new Date()).getTime();
        Date accessTokenExpirationTime = new Date(currentTime + this.accessTokenValidationInMilliseconds);
        String tokenId = UUID.randomUUID().toString();

        String accessToken = Jwts.builder()
                .setSubject(member.getLoginId())
                .claim(USERNAME_KEY, member.getName())
                .claim(TOKEN_ID_KEY, tokenId)
                .signWith(hashKey, SignatureAlgorithm.HS512)
                .setExpiration(accessTokenExpirationTime)
                .compact();

        return TokenInfo.builder()
                .ownerLoginId(member.getLoginId())
                .tokenId(tokenId)
                .accessToken(accessToken)
                .accessTokenExpireTime(accessTokenExpirationTime)
                .build();
    }

    public TokenValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(hashKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
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

}
