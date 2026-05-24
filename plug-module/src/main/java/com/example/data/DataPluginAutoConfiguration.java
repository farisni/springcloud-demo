package com.example.data;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.data")
public class DataPluginAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "spring.datasource", name = "url")
    public static class MyBatisPlusPluginConfiguration {

        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor() {
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
            pagination.setMaxLimit(1000L);
            interceptor.addInnerInterceptor(pagination);
            return interceptor;
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
    public static class RedisPluginConfiguration {

        @Bean(destroyMethod = "shutdown")
        public RedissonClient redissonClient() {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://localhost:6379")
                    .setConnectionPoolSize(32)
                    .setConnectionMinimumIdleSize(8);
            config.setCodec(new JsonJacksonCodec());
            return Redisson.create(config);
        }
    }
}
