# Spring Cloud 条件化数据插件 Demo

基于 Spring Boot 3.5.1 + Spring Cloud 2025.0.1，演示「一个插件模块，按 YAML 配置动态启用 MyBatis-Plus / Redis」的架构模式。

## 项目结构

```
springcloud-demo/
├── pom.xml            # 父 POM，统一版本管理
├── plug-module/       # 数据插件（包含 MP + Redis，条件激活）
└── web-module/        # Web 应用（依赖 plug-module，YAML 控制开关）
```

## 核心机制

`plug-module` 通过 `@ConditionalOnProperty` 实现按需激活：

| 条件 | 激活组件 |
|------|---------|
| 配置 `spring.datasource.url` | MyBatis-Plus（分页插件 + MapperScan） |
| 配置 `spring.data.redis.host` | RedisTemplate（Jackson2Json 序列化） |

两个组件**完全独立**，可单独启用、同时启用，或都不启用。

> **注意：** 默认已排除 `DataSourceAutoConfiguration`，启用 MyBatis-Plus 时记得删除 `autoconfigure.exclude` 段落。

## 使用方法

### 场景一：仅启用 Redis（默认）

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

### 场景二：仅启用 MyBatis-Plus

```yaml
spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://192.168.8.112:5432/youhaowu_pms
    username: faris
    password: "123456"
```

### 场景三：同时启用

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

### 场景四：都不启用

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

不配置任何数据源信息，Web 模块正常启动，插件零作用。

## 验证端点

启动 `web-module` 后访问：

```bash
# 测试 Redis
curl http://localhost:8080/demo/redis/test

# 测试 MyBatis-Plus
curl http://localhost:8080/demo/db/test
```

未启用时返回 `{"status": "disabled", "message": "..."} `，启用后返回正常结果。

## 版本

| 组件 | 版本 |
|------|------|
| Spring Boot | 3.5.1 |
| Spring Cloud | 2025.0.1 |
| MyBatis-Plus | 3.5.11 |
| PostgreSQL 驱动 | 自动管理 |
| Redis 客户端 | Lettuce（Boot 默认） |
