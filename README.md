# Spring Cloud 条件化数据插件 Demo

基于 Spring Boot 3.5.1 + Spring Cloud 2025.0.1，演示「只写 YAML 配置，按需启用 MyBatis-Plus / Redisson / Kafka」的插件架构。

## 项目结构

```
springcloud-demo/
├── pom.xml            # 父 POM，统一版本管理
├── plug-module/       # 数据插件（自动识别 YAML，零排除）
└── web-module/        # Web 应用（引入依赖 + 写 YAML 即可）
```

### plug-module 内部结构

```
plug-module/src/main/java/com/example/plug/
├── MybatisPlusPluginConfiguration.java   # spring.datasource.url → MybatisPlusInterceptor
├── RedisPluginConfiguration.java         # spring.data.redis.host → RedissonClient
├── KafkaPluginConfiguration.java         # spring.kafka.bootstrap-servers → KafkaTemplate
├── PluginEnvironmentPostProcessor.java   # 自动排除 DataSourceAutoConfiguration
└── service/
    └── RedisCacheService.java            # 通用 Redis 缓存工具
```

## 核心理念

**只需写 YAML 配置，一行 exclude 都不用写。**

| YAML 配置 | 自动激活 |
|----------|---------|
| `spring.data.redis.host` | RedissonClient（JsonJacksonCodec，连接池 32/8） |
| `spring.datasource.url` | MyBatis-Plus（PostgreSQL 分页插件，单页上限 1000） |
| `spring.kafka.bootstrap-servers` | KafkaTemplate（StringSerializer） |

三个组件完全独立，配哪个启哪个，都配都启，都不配零作用。

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

### 仅启用 Kafka

```yaml
spring:
  kafka:
    bootstrap-servers: 192.168.8.112:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### 全部启用

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.8.112:5432/youhaowu_ums
    username: faris
    password: "123456"
  data:
    redis:
      host: 192.168.8.112
      port: 6379
  kafka:
    bootstrap-servers: 192.168.8.112:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

## 验证端点

```bash
curl http://localhost:8080/demo/redis/test
curl http://localhost:8080/demo/member/list
```

## 实现方案

### 整体调用链

```
应用启动
  → PluginEnvironmentPostProcessor 检查 spring.datasource.url
      没配 → 自动排除 DataSourceAutoConfiguration
      配了 → 放行
  → 三个 PluginConfiguration 各自独立加载
      @ConditionalOnProperty 检查 YAML 配置
      配了 → 注入对应 Bean
      没配 → 跳过
  → 应用正常启动
```

### 条件注入：`@ConditionalOnProperty`

每个插件一个独立的 `@Configuration` 类，通过 `@ConditionalOnProperty` 做开关，注册在 `AutoConfiguration.imports`：

| 配置类 | 激活条件 | 注入的 Bean |
|--------|---------|------------|
| `MybatisPlusPluginConfiguration` | `spring.datasource.url` | `MybatisPlusInterceptor` |
| `RedisPluginConfiguration` | `spring.data.redis.host` | `RedissonClient` |
| `KafkaPluginConfiguration` | `spring.kafka.bootstrap-servers` | `KafkaTemplate`（Boot 自动创建） |

每个类都带 `@ComponentScan("com.example.plug")`，确保 `RedisCacheService` 等工具类能被扫描到。重复扫描不会冲突，Spring 会去重。

### 透明排除：`PluginEnvironmentPostProcessor`

`DataSourceAutoConfiguration` 是 Spring Boot 内置的，只要 classpath 上有 JDBC 驱动就会无条件初始化。`PluginEnvironmentPostProcessor` 在所有自动配置之前检查 `spring.datasource.url`——没配就自动把 `DataSourceAutoConfiguration` 加入排除列表，对使用者完全透明。

`KafkaAutoConfiguration` 在缺配置时不会导致启动失败，因此无需额外排除。

注册方式：`META-INF/spring.factories`

```
org.springframework.boot.env.EnvironmentPostProcessor=com.example.plug.PluginEnvironmentPostProcessor
```

### RedissonClient 连接地址

不硬编码，从 `Environment` 读取 `spring.data.redis.host` 和 `port`，拼接为 `redis://host:port`。改配置不用改代码。

> MyBatis-Plus 3.5.11 将 `PaginationInnerInterceptor` 从 `mybatis-plus-extension` 移到了 `mybatis-plus-jsqlparser`，需显式引入该依赖。

## 版本

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.1 |
| Spring Cloud | 2025.0.1 |
| MyBatis-Plus | 3.5.11 |
| Redisson | 3.40.2 |
| Spring Kafka | 由 Boot BOM 管理 |
| PostgreSQL 驱动 | 自动管理 |
