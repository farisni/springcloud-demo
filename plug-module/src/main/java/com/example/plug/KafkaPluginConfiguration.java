package com.example.plug;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers")
@ComponentScan(basePackages = "com.example.plug")
public class KafkaPluginConfiguration {
    // KafkaTemplate 由 Spring Boot 自动创建，此处作为条件开关
}
