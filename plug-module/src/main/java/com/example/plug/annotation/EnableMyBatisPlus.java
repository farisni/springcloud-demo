package com.example.plug.annotation;

import com.example.plug.MybatisPlusPluginConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用 MyBatis-Plus 插件（可选使用）。
 *
 * <p>加在 {@code @SpringBootApplication} 类上即可显式导入，
 * 等价于在 YAML 中配置 {@code spring.datasource.url}。
 * 不加此注解也可通过 YAML 自动激活。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MybatisPlusPluginConfiguration.class)
public @interface EnableMyBatisPlus {
}
