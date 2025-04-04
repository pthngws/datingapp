package com.example.mobile.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public void saveRefreshToken(ObjectId userId, String refreshToken, long ttlSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofSeconds(ttlSeconds));
    }

    public String getRefreshToken(ObjectId userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(ObjectId userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
