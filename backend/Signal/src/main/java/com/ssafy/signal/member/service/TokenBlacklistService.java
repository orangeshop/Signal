package com.ssafy.signal.member.service;

import com.ssafy.signal.member.domain.TokenBlacklist;
import com.ssafy.signal.member.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void blacklistToken(String token, LocalDateTime expirationTime) {
        log.debug("Blacklisting token: {}", token);
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, expirationTime);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.findByToken(token).isPresent();
    }
}
