package com.example.demo.lottery.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void addToBlacklist(String jti, long expiration) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "true", expiration, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void removeFromBlacklist(String token) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
    }
}