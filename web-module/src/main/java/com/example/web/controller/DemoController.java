package com.example.web.controller;

import com.example.plug.service.RedisCacheService;
import com.example.web.entity.Member;
import com.example.web.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    @Autowired(required = false)
    private RedisCacheService redisCacheService;

    @Autowired(required = false)
    private MemberMapper memberMapper;

    @GetMapping("/redis/test")
    public Map<String, Object> testRedis() {
        if (redisCacheService == null) {
            return Map.of("status", "disabled",
                    "message", "Redis 未启用，请配置 spring.data.redis.host");
        }

        try {
            redisCacheService.set("demo::hello", "Hello from plug-module Redisson!", 60);
            Object value = redisCacheService.get("demo::hello");
            redisCacheService.delete("demo::hello");
            return Map.of("status", "ok", "value", String.valueOf(value));
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/member/list")
    public Map<String, Object> listMembers() {
        if (memberMapper == null) {
            return Map.of("status", "disabled",
                    "message", "MyBatis-Plus 未启用，请配置 spring.datasource.url");
        }

        try {
            List<Member> members = memberMapper.selectList(null);
            return Map.of("status", "ok", "count", members.size(), "data", members);
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    @GetMapping("/member/{id}")
    public Map<String, Object> getMember(@PathVariable Long id) {
        if (memberMapper == null) {
            return Map.of("status", "disabled",
                    "message", "MyBatis-Plus 未启用，请配置 spring.datasource.url");
        }

        try {
            Member member = memberMapper.selectById(id);
            if (member == null) {
                return Map.of("status", "error", "message", "成员不存在");
            }
            return Map.of("status", "ok", "data", member);
        } catch (Exception e) {
            return Map.of("status", "error", "message", e.getMessage());
        }
    }
}
