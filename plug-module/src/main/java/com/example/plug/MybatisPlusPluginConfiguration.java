package com.example.plug;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件自动配置。
 *
 * <p>激活条件：YAML 中配置了 {@code spring.datasource.url}。
 * 未配置时跳过，不会创建任何 MyBatis 相关 Bean。
 *
 * <p>注入的 Bean：
 * <ul>
 *   <li>{@link MybatisPlusInterceptor} — 分页插件（PostgreSQL 方言，单页上限 1000）</li>
 * </ul>
 *
 * <p>注意：MyBatis-Plus 3.5.11 将 {@code PaginationInnerInterceptor}
 * 从 {@code mybatis-plus-extension} 移到了 {@code mybatis-plus-jsqlparser}，
 * 需在 pom.xml 中显式引入该依赖。
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "url")
@ComponentScan(basePackages = "com.example.plug")
public class MybatisPlusPluginConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        pagination.setMaxLimit(1000L);
        interceptor.addInnerInterceptor(pagination);
        return interceptor;
    }
}
