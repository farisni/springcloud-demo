# Spring Cloud 条件化数据插件 Demo

基于 Spring Boot 3.5.1 + Spring Cloud 2025.0.1，演示「只写 YAML 配置，按需启用 MyBatis-Plus / Redisson」的插件架构。

## 项目结构

```
springcloud-demo/
├── pom.xml            # 父 POM，统一版本管理
├── plug-module/       # 数据插件（自动识别 YAML，零排除）
└── web-module/        # Web 应用（引入依赖 + 写 YAML 即可）
```

## 核心理念

**只需写 YAML 配置，无需任何排除、无需任何注解。**

| YAML 配置 | 自动激活 |
|----------|---------|
| 配了 `spring.data.redis.host` | RedissonClient（JsonJacksonCodec） |
| 配了 `spring.datasource.url` | MyBatis-Plus（分页插件 + MapperScan） |

两个组件完全独立，配哪个启哪个，都配都启，都不配零作用。

## 使用方法

### 仅启用 Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 仅启用 MyBatis-Plus

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.8.112:5432/youhaowu_pms
    username: faris
    password: "123456"
```

### 同时启用

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.8.112:5432/youhaowu_pms
    username: faris
    password: "123456"
  data:
    redis:
      host: localhost
      port: 6379
```

## 验证端点

```bash
curl http://localhost:8080/demo/redis/test
curl http://localhost:8080/demo/db/test
```

## 实现方案

### 整体调用链

```
应用启动
  → EnvironmentPostProcessor 检查 spring.datasource.url
      没配 → 自动排除 DataSourceAutoConfiguration
      配了 → 放行，DataSource 正常初始化
  → DataPluginAutoConfiguration 加载
      @ConditionalOnProperty 检查 YAML 配置
      配了 → 注入对应 Bean
      没配 → 跳过
  → 应用正常启动
```

### DataPluginAutoConfiguration — 条件注入

顶层 `@Configuration` + `@ComponentScan("com.example.data")`，内嵌两个独立内部类：

- `MyBatisPlusPluginConfiguration` — `@ConditionalOnProperty(prefix = "spring.datasource", name = "url")`，激活时注册 `MybatisPlusInterceptor` 分页插件 + `@MapperScan`
- `RedisPluginConfiguration` — `@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")`，激活时注册 `RedissonClient`（`JsonJacksonCodec`，单机模式，连接池 32/8）

选 `@ConditionalOnProperty` 而非自定义注解的理由：插件使用者唯一需要关心的就是 YAML 配置，配了就启、不配就关，最符合直觉。

### DataPluginEnvironmentPostProcessor — 透明排除

`DataSourceAutoConfiguration` 是 Spring Boot 内置的，只要 classpath 上存在 JDBC 驱动就会无条件初始化。`plug-module` 依赖了 `mybatis-plus-spring-boot3-starter`，它会传递引入 JDBC，因此即使没配 `spring.datasource.url`，`DataSourceAutoConfiguration` 也会触发并报错。

解决方式：`EnvironmentPostProcessor` 比自动配置更早执行，在 Environment 准备阶段检查 `spring.datasource.url` 是否已配置——没配就自动把 `DataSourceAutoConfiguration` 加入排除列表。对使用者完全透明，不需要在 YAML 里写任何 `exclude`。

注册方式：`META-INF/spring.factories`

```
org.springframework.boot.env.EnvironmentPostProcessor=com.example.data.DataPluginEnvironmentPostProcessor
```

> MyBatis-Plus 3.5.11 将 `PaginationInnerInterceptor` 从 `mybatis-plus-extension` 移到了 `mybatis-plus-jsqlparser`，需要显式引入该依赖。

## 版本

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.1 |
| Spring Cloud | 2025.0.1 |
| MyBatis-Plus | 3.5.11 |
| Redisson | 3.40.2 |
| PostgreSQL 驱动 | 自动管理 |
