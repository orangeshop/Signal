package com.ssafy.signal.member.service;

import com.ssafy.signal.member.domain.TokenBlacklist;
import com.ssafy.signal.member.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    private final RedisTemplate<String, Object> redisBlackListTemplate;

    public void blacklistToken(String token, LocalDateTime expirationTime) {
        log.debug("Blacklisting token: {}", token);
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, expirationTime);
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    public boolean isTokenBlacklisted(String token) {
        token = token.substring(7);
        Optional<TokenBlacklist> tokenBlacklistOptional = tokenBlacklistRepository.findByToken(token);
        if (tokenBlacklistOptional.isPresent()) {
            TokenBlacklist tokenBlacklist = tokenBlacklistOptional.get();
            if (tokenBlacklist.getExpirationTime().isAfter(LocalDateTime.now())) {
                return true;
            } else {
                log.debug("Removing expired token from blacklist: {}", token);
                tokenBlacklistRepository.delete(tokenBlacklist);
            }
        }
        return false;
    }
}
