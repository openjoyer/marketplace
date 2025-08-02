package com.openjoyer.authservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStorageService {
    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hashOps;

    public void storeSessionToken(String userId, String deviceIP, String token, long ttl) {
        String key = "session:" + userId;
        hashOps.put(key, deviceIP, token);

        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
    }

    public Map<String, String> getAllSessions(String userId) {
        String key = "session:" + userId;
        return hashOps.entries(key);
    }

    public boolean hasSession(String userId, String deviceIP) {
        String key = "session:" + userId;
        return hashOps.hasKey(key, deviceIP);
    }

    public String getSession(String userId, String deviceIP) {
        String key = "session:" + userId;
        return hashOps.get(key, deviceIP);
    }

    public void invalidateToken(String userId, String deviceIP) {
        String key = "session:" + userId;
        hashOps.delete(key, deviceIP);
    }

    public void invalidateAllUserTokens(String userId) {
        String key = "session:" + userId;
        redisTemplate.delete(key);
    }
}