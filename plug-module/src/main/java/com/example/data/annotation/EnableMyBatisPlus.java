package com.example.data.annotation;

import com.example.data.DataPluginAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DataPluginAutoConfiguration.MyBatisPlusPluginConfiguration.class)
public @interface EnableMyBatisPlus {
}
