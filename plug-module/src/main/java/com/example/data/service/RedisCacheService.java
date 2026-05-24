package com.example.data.service;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCacheService {

    @Autowired(required = false)
    private RedissonClient redissonClient;

    public void set(String key, Object value, long timeoutSeconds) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, Duration.ofSeconds(timeoutSeconds));
    }

    public Object get(String key) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public boolean delete(String key) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        return redissonClient.getBucket(key).delete();
    }
}
