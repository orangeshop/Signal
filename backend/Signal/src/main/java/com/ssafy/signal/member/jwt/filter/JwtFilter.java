package com.ssafy.signal.member.jwt.filter;

import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.jwt.token.TokenStatus;
import com.ssafy.signal.member.jwt.token.dto.TokenValidationResult;
import com.ssafy.signal.member.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_\\.\\-\\+\\/=]*)";

//    private static final String BEARER_PREFIX = "Bearer ([a-zA-Z0-9_\\\\-\\\\+\\\\/=]+)\\\\.([a-zA-Z0-9_\\\\-\\\\+\\\\/=]+)\\\\.([a-zA-Z0-9_.\\\\-\\\\+\\\\/=]*)";
    private static final Pattern BEAER_PATTERN = Pattern.compile(BEARER_PREFIX);
    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (!StringUtils.hasText(token) || tokenBlacklistService.isTokenBlacklisted(token)) {
            handleMissingToken(request, response, filterChain);
            return;
        }
        TokenValidationResult tokenValidationResult = tokenProvider.validateToken(token);

        if (!tokenValidationResult.isValid()) {
            handleWrongToken(request, response, filterChain, tokenValidationResult);
            return;
        }

        handleValidToken(token, tokenValidationResult.getClaims());
        filterChain.doFilter(request, response);
    }

    private void handleValidToken(String token, Claims claims) {
        Authentication authentication = tokenProvider.getAuthentication(token, claims);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("AUTH SUCCESS : {}", authentication.getName());
    }

    private void handleWrongToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, TokenValidationResult tokenValidationResult) throws ServletException, IOException {
        request.setAttribute("result", tokenValidationResult);
        filterChain.doFilter(request, response);
    }

    private void handleMissingToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.setAttribute("result", new TokenValidationResult(TokenStatus.WRONG_AUTH_HEADER, null, null, null)
        );
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("Authorization Header: {}", bearerToken);
        if (bearerToken != null) {
            Matcher matcher = BEAER_PATTERN.matcher(bearerToken);
            if (matcher.matches()) {
                String token = matcher.group(0).substring(7);
                log.debug("Extracted Token: {}", token);
                return token;
            }
        }
        return null;
    }
}
