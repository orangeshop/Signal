package com.ssafy.signal.member.jwt.config;

import com.ssafy.signal.member.jwt.AccessTokenBlackList;
import com.ssafy.signal.member.jwt.JwtAccessDeniedHandler;
import com.ssafy.signal.member.jwt.JwtAuthenticationEntryPoint;
import com.ssafy.signal.member.jwt.JwtProperties;
import com.ssafy.signal.member.jwt.token.TokenProvider;
import com.ssafy.signal.member.repository.MemberRepository;
import com.ssafy.signal.member.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    private final AccessTokenBlackList accessTokenBlackList;

    @Bean
    public TokenProvider tokenProvider(JwtProperties jwtProperties, MemberRepository memberRepository, TokenBlacklistService tokenBlacklistService) {
        return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds(), memberRepository, tokenBlacklistService, accessTokenBlackList);
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }
}
