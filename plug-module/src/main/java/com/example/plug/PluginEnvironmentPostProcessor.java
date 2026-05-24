package com.example.plug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件环境后处理器，在所有自动配置之前执行。
 *
 * <h3>为什么需要它</h3>
 * plug-module 依赖了 mybatis-plus-spring-boot3-starter，会传递引入 JDBC 驱动。
 * Spring Boot 内置的 DataSourceAutoConfiguration 只要 classpath 上有 JDBC
 * 就会无条件尝试创建 DataSource，没配 url 时直接报错导致启动失败。
 * 这个处理器在自动配置之前检查配置，没配就自动排除，对使用者完全透明。
 *
 * <h3>执行时机</h3>
 * Environment 准备完成后 → 本处理器 → 自动配置加载
 *
 * <h3>注册方式</h3>
 * META-INF/spring.factories
 */
public class PluginEnvironmentPostProcessor implements EnvironmentPostProcessor {

    /** Spring Boot 内置的 DataSource 自动配置全限定类名 */
    private static final String DATASOURCE_AUTO_CONFIG =
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 1. 已配置数据源 URL → 什么都不做，DataSourceAutoConfiguration 正常运行
        String url = environment.getProperty("spring.datasource.url");
        if (url != null && !url.isEmpty()) {
            return;
        }

        // 2. 检查当前是否已经排除过（用户手动配置了 exclude）
        String excludes = environment.getProperty("spring.autoconfigure.exclude", "");
        if (excludes.contains(DATASOURCE_AUTO_CONFIG)) {
            return;
        }

        // 3. 未配置 URL 且未被排除 → 自动追加到排除列表
        String newExcludes = excludes.isEmpty()
                ? DATASOURCE_AUTO_CONFIG
                : excludes + "," + DATASOURCE_AUTO_CONFIG;

        // 4. 以最高优先级注入 Environment，确保自动配置读取到排除项
        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = new HashMap<>();
        map.put("spring.autoconfigure.exclude", newExcludes);
        sources.addFirst(new MapPropertySource("plug-excludes", map));
    }
}
