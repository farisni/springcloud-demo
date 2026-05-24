package com.example.plug;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Redis（Redisson）插件自动配置。
 *
 * <p>激活条件：YAML 中配置了 {@code spring.data.redis.host}。
 * 未配置时跳过，不会创建 {@link RedissonClient}。
 *
 * <h3>注入的 Bean</h3>
 * <ul>
 *   <li>{@link RedissonClient} — 单机模式，连接池 32/8，JsonJacksonCodec 序列化</li>
 * </ul>
 *
 * <h3>连接地址</h3>
 * 从 {@link Environment} 读取 {@code spring.data.redis.host} 和 {@code port}，
 * 拼接为 {@code redis://host:port}，改配置无需改代码。
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
@ComponentScan(basePackages = "com.example.plug")
public class RedisPluginConfiguration {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(Environment env) {
        String host = env.getProperty("spring.data.redis.host", "localhost");
        String port = env.getProperty("spring.data.redis.port", "6379");
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setConnectionPoolSize(32)
                .setConnectionMinimumIdleSize(8);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}
