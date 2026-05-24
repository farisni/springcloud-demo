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
