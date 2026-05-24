package com.example.plug;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Kafka 插件自动配置。
 *
 * <p>激活条件：YAML 中配置了 {@code spring.kafka.bootstrap-servers}。
 * 未配置时跳过。
 *
 * <h3>注入的 Bean</h3>
 * {@code KafkaTemplate} 由 Spring Boot 内置的 {@code KafkaAutoConfiguration}
 * 自动创建，本类仅作为条件开关，确保未配置时不初始化 Kafka 相关组件。
 *
 * <h3>与 DataSourceAutoConfiguration 的区别</h3>
 * Kafka 在缺配置时不会导致启动失败，因此无需在
 * {@link PluginEnvironmentPostProcessor} 中额外排除。
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers")
@ComponentScan(basePackages = "com.example.plug")
public class KafkaPluginConfiguration {
    // KafkaTemplate 由 Spring Boot 自动创建，此处作为条件开关
}
