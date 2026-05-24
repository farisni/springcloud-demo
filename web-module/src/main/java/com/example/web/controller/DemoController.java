package com.example.web.controller;

import com.example.data.entity.User;
import com.example.data.mapper.UserMapper;
import com.example.data.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired(required = false)
    private RedisCacheService redisCacheService;

    @Autowired(required = false)
    private UserMapper userMapper;

    @GetMapping("/redis/test")
    public Map<String, Object> testRedis() {
        if (redisCacheService == null) {
            return Map.of("status", "disabled",
                    "message", "Redis 未启用，请配置 spring.data.redis.host");
        }

        try {
            redisCacheService.set("demo::hello", "Hello from plug-module Redis!", 60, TimeUnit.SECONDS);
            Object value = redisCacheService.get("demo::hello");
            redisCacheService.delete("demo::hello");
            return Map.of("status", "ok", "value", String.valueOf(value));
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/db/test")
    public Map<String, Object> testDb() {
        if (userMapper == null) {
            return Map.of("status", "disabled",
                    "message", "MyBatis-Plus 未启用，请配置 spring.datasource.url");
        }

        try {
            List<User> users = userMapper.selectList(null);
            return Map.of("status", "ok", "count", users.size());
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
