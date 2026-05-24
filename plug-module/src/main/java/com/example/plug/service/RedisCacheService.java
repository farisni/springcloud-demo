package com.example.plug.service;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 通用 Redis 缓存服务，封装 Redisson 的 RBucket 操作。
 *
 * <p>RedissonClient 使用 {@code @Autowired(required = false)} 注入：
 * 如果 Redis 插件未启用（未配置 {@code spring.data.redis.host}），
 * 本 Service 仍然会被创建，但所有方法调用都会抛出明确的异常提示。
 *
 * <p>提供三个基础操作：写入（带 TTL）、读取、删除。
 */
@Service
public class RedisCacheService {

    @Autowired(required = false)
    private RedissonClient redissonClient;

    /**
     * 写入缓存，带过期时间。
     *
     * @param key            缓存键
     * @param value          缓存值
     * @param timeoutSeconds 过期秒数
     */
    public void set(String key, Object value, long timeoutSeconds) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * 读取缓存。
     *
     * @param key 缓存键
     * @return 缓存值，键不存在时返回 null
     */
    public Object get(String key) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 删除缓存。
     *
     * @param key 缓存键
     * @return true 表示删除成功
     */
    public boolean delete(String key) {
        if (redissonClient == null) {
            throw new IllegalStateException("Redis 未启用，请配置 spring.data.redis.host");
        }
        return redissonClient.getBucket(key).delete();
    }
}
