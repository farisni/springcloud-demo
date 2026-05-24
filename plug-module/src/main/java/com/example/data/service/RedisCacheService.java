package com.example.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCacheService {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        if (redisTemplate == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        if (redisTemplate == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
