package com.example.data;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ComponentScan(basePackages = "com.example.data")
public class DataPluginAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "spring.datasource", name = "url")
    @MapperScan("com.example.data.mapper")
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

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                    ObjectMapper.DefaultTyping.NON_FINAL);

            Jackson2JsonRedisSerializer<Object> jacksonSerializer =
                    new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(jacksonSerializer);
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(jacksonSerializer);
            template.afterPropertiesSet();

            return template;
        }
    }
}
