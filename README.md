# Spring Cloud 条件化数据插件 Demo

基于 Spring Boot 3.5.1 + Spring Cloud 2025.0.1，演示「只写 YAML 配置，按需启用 MyBatis-Plus / Redisson / Kafka」的插件架构。

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
| 配了 `spring.kafka.bootstrap-servers` | KafkaTemplate（String 序列化） |

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
  → EnvironmentPostProcessor 检查 spring.datasource.url
      没配 → 自动排除 DataSourceAutoConfiguration
      配了 → 放行，DataSource 正常初始化
  → MybatisPlusPluginConfiguration / RedisPluginConfiguration / KafkaPluginConfiguration 各自独立加载
      @ConditionalOnProperty 检查 YAML 配置
      配了 → 注入对应 Bean
      没配 → 跳过
  → 应用正常启动
```

### 三个独立配置类

每个插件一个独立的 `@Configuration` 类，注册在 `AutoConfiguration.imports`：

- **`MybatisPlusPluginConfiguration`** — `@ConditionalOnProperty(prefix = "spring.datasource", name = "url")`，注入 `MybatisPlusInterceptor` 分页插件
- **`RedisPluginConfiguration`** — `@ConditionalOnProperty(prefix = "spring.data.redis", name = "host")`，注入 `Environment` 读取 host/port，创建 `RedissonClient`（`JsonJacksonCodec`，连接池 32/8）
- **`KafkaPluginConfiguration`** — `@ConditionalOnProperty(prefix = "spring.kafka", name = "bootstrap-servers")`，`KafkaTemplate` 由 Spring Boot 自动创建，此处仅作为条件开关

选 `@ConditionalOnProperty` 的理由：插件使用者唯一需要关心的就是 YAML 配置，配了就启、不配就关，最符合直觉。

### PluginEnvironmentPostProcessor — 透明排除

`DataSourceAutoConfiguration` 只要 classpath 上有 JDBC 驱动就会无条件初始化并可能报错。`EnvironmentPostProcessor` 在自动配置之前检查 `spring.datasource.url`，没配就自动排除。

> `KafkaAutoConfiguration` 不会在缺配置时导致启动失败，因此无需额外排除。

注册方式：`META-INF/spring.factories`

```
org.springframework.boot.env.EnvironmentPostProcessor=com.example.plug.PluginEnvironmentPostProcessor
```

> MyBatis-Plus 3.5.11 将 `PaginationInnerInterceptor` 从 `mybatis-plus-extension` 移到了 `mybatis-plus-jsqlparser`，需要显式引入该依赖。

## 版本

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.1 |
| Spring Cloud | 2025.0.1 |
| MyBatis-Plus | 3.5.11 |
| Redisson | 3.40.2 |
| Spring Kafka | 由 Boot BOM 管理 |
| PostgreSQL 驱动 | 自动管理 |
