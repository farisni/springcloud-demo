package com.example.plug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashMap;
import java.util.Map;

/**
 * 智能排除 DataSourceAutoConfiguration：
 * 当未配置 spring.datasource.url 时，自动将其加入排除列表，
 * 避免 "Failed to configure a DataSource" 启动报错。
 */
public class PluginEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String DATASOURCE_AUTO_CONFIG =
            "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = environment.getProperty("spring.datasource.url");
        if (url != null && !url.isEmpty()) {
            return;
        }

        String excludes = environment.getProperty("spring.autoconfigure.exclude", "");
        if (excludes.contains(DATASOURCE_AUTO_CONFIG)) {
            return;
        }

        String newExcludes = excludes.isEmpty()
                ? DATASOURCE_AUTO_CONFIG
                : excludes + "," + DATASOURCE_AUTO_CONFIG;

        MutablePropertySources sources = environment.getPropertySources();
        Map<String, Object> map = new HashMap<>();
        map.put("spring.autoconfigure.exclude", newExcludes);
        sources.addFirst(new MapPropertySource("data-plugin-excludes", map));
    }
}
