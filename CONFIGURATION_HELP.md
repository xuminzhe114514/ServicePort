# 项目配置文档

## 文档概述

本文档旨在提供项目配置的详细说明，包括项目依赖配置（pom.xml）和应用运行配置（application.properties）。通过本文档，开发人员可以了解项目的配置结构、各项配置的作用以及如何根据实际需求修改配置。

## 1. 项目依赖配置 (pom.xml)

### 1.1 项目基本信息

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| groupId | com.example | 项目组ID |
| artifactId | demo | 项目标识符 |
| version | 0.0.1-SNAPSHOT | 项目版本 |
| name | demo | 项目名称 |
| description | demo | 项目描述 |
| parent | spring-boot-starter-parent:4.0.1 | Spring Boot父依赖，提供依赖管理 |
| java.version | 17 | Java运行版本 |

### 1.2 核心依赖

| 依赖名称 | 版本 | 描述 | 用途 |
| -------- | --- | ---- | ---- |
| spring-boot-starter-data-jpa | 4.0.1 | Spring Boot JPA启动器 | 提供JPA数据访问支持 |
| spring-boot-starter-security | 4.0.1 | Spring Boot安全启动器 | 提供Spring Security安全框架 |
| spring-boot-starter-validation | 4.0.1 | Spring Boot验证启动器 | 提供Bean验证支持 |
| spring-boot-starter-webmvc | 4.0.1 | Spring Boot WebMVC启动器 | 提供Web MVC框架支持 |
| springdoc-openapi-starter-webmvc-ui | 2.5.0 | SpringDoc OpenAPI启动器 | 自动生成API文档 |

### 1.3 开发和运行依赖

| 依赖名称 | 版本 | 描述 | 用途 |
| -------- | --- | ---- | ---- |
| spring-boot-devtools | 4.0.1 | Spring Boot开发工具 | 提供热部署等开发便利特性 |
| mysql-connector-j | 运行时 | MySQL JDBC驱动 | 连接MySQL数据库 |
| lombok | 可选 | Lombok工具 | 简化Java代码，自动生成getter/setter等方法 |

### 1.4 测试依赖

| 依赖名称 | 版本 | 描述 | 用途 |
| -------- | --- | ---- | ---- |
| spring-boot-starter-data-jpa-test | 4.0.1 | JPA测试启动器 | JPA相关测试支持 |
| spring-boot-starter-security-test | 4.0.1 | 安全测试启动器 | Spring Security测试支持 |
| spring-boot-starter-validation-test | 4.0.1 | 验证测试启动器 | Bean验证测试支持 |
| spring-boot-starter-webmvc-test | 4.0.1 | WebMVC测试启动器 | WebMVC测试支持 |

### 1.5 构建插件配置

| 插件名称 | 版本 | 描述 | 配置项 |
| -------- | --- | ---- | ------ |
| maven-compiler-plugin | 默认 | Maven编译器插件 | 配置注解处理器路径，包括spring-boot-configuration-processor和lombok |
| spring-boot-maven-plugin | 默认 | Spring Boot Maven插件 | 配置排除lombok依赖 |

## 2. 应用运行配置 (application.properties)

### 2.1 应用基本信息

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| spring.application.name | demo | 应用名称 |

### 2.2 数据库配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| spring.datasource.url | jdbc:mysql://localhost:3306/pharmacy_inventory?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true | MySQL数据库连接URL |
| spring.datasource.username | root | 数据库用户名 |
| spring.datasource.password | 114514aA@ | 数据库密码 |
| spring.datasource.driver-class-name | com.mysql.cj.jdbc.Driver | MySQL JDBC驱动类 |

### 2.3 Hikari 连接池配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| spring.datasource.hikari.maximum-pool-size | 10 | 最大连接池大小 |
| spring.datasource.hikari.minimum-idle | 5 | 最小空闲连接数 |
| spring.datasource.hikari.idle-timeout | 30000 | 空闲连接超时时间（毫秒） |
| spring.datasource.hikari.connection-timeout | 30000 | 连接超时时间（毫秒） |
| spring.datasource.hikari.max-lifetime | 1800000 | 连接最大生命周期（毫秒） |

### 2.4 JPA 配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| spring.jpa.hibernate.ddl-auto | update | Hibernate DDL自动生成策略 |
| spring.jpa.show-sql | true | 是否显示SQL语句 |
| spring.jpa.properties.hibernate.format_sql | true | 是否格式化SQL语句 |
| spring.jpa.open-in-view | false | 是否在视图中打开JPA会话 |

### 2.5 服务器配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| server.port | 8081 | 服务器端口 |
| server.servlet.context-path | /api | 服务器上下文路径 |

### 2.6 文件上传配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| spring.servlet.multipart.max-file-size | 10MB | 单个文件最大上传大小 |
| spring.servlet.multipart.max-request-size | 10MB | 单个请求最大上传大小 |

### 2.7 日志配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| logging.level.com.example.demo | DEBUG | 应用包日志级别 |
| logging.level.org.springframework.web | INFO | Spring Web日志级别 |
| logging.level.org.hibernate.SQL | DEBUG | Hibernate SQL日志级别 |
| logging.level.org.hibernate.type.descriptor.sql.BasicBinder | TRACE | Hibernate参数绑定日志级别 |
| logging.level.org.springframework.security | DEBUG | Spring Security日志级别 |
| logging.level.org.springframework.boot.autoconfigure.security | DEBUG | Spring Boot安全自动配置日志级别 |

### 2.8 JWT 配置

| 配置项 | 值 | 描述 |
| ------ | --- | ---- |
| jwt.secret | pharmacy-inventory-system-secret-key-2025-must-be-at-least-256-bits-long | JWT签名密钥 |
| jwt.expiration | 86400000 | JWT过期时间（毫秒） |
| jwt.header | Authorization | JWT请求头名称 |
| jwt.prefix | Bearer | JWT前缀 |

## 3. 配置使用说明

### 3.1 如何修改配置

#### 3.1.1 修改 pom.xml

1. **添加新依赖**：在 `<dependencies>` 标签内添加新的 `<dependency>` 节点
2. **修改依赖版本**：修改 `<version>` 标签内的版本号
3. **修改Java版本**：修改 `<java.version>` 标签内的版本号
4. **修改Spring Boot版本**：修改 `<parent>` 标签内的 `<version>` 标签

#### 3.1.2 修改 application.properties

1. **直接编辑**：使用文本编辑器直接修改配置项的值
2. **环境变量覆盖**：可以使用环境变量覆盖配置项，格式为 `SPRING_APPLICATION_NAME`（对应 `spring.application.name`）
3. **命令行参数覆盖**：启动应用时可以通过命令行参数覆盖配置，格式为 `--spring.application.name=demo`

### 3.2 配置项的作用和影响

#### 3.2.1 数据库配置

- **spring.datasource.url**：指定数据库连接地址，修改时需要确保数据库服务可用
- **spring.datasource.username/password**：数据库认证信息，修改时需要确保用户存在且有相应权限
- **spring.datasource.hikari.***：连接池配置，影响数据库连接的性能和稳定性

#### 3.2.2 JPA 配置

- **spring.jpa.hibernate.ddl-auto**：影响数据库表结构的自动生成策略
  - `update`：仅更新表结构，不删除现有数据
  - `create`：每次启动创建新表，删除现有数据
  - `create-drop`：启动时创建表，关闭时删除表
  - `validate`：仅验证表结构，不做修改
- **spring.jpa.show-sql**：开发环境建议设置为 `true`，生产环境建议设置为 `false`

#### 3.2.3 服务器配置

- **server.port**：修改服务器监听端口，需要确保端口未被占用
- **server.servlet.context-path**：修改API访问路径前缀

#### 3.2.4 日志配置

- **logging.level.***：调整不同包的日志级别，影响日志输出的详细程度
  - `DEBUG`：详细日志，适合开发环境
  - `INFO`：一般信息，适合生产环境
  - `WARN`：警告信息
  - `ERROR`：错误信息

#### 3.2.5 JWT 配置

- **jwt.secret**：JWT签名密钥，生产环境必须修改为强密钥
- **jwt.expiration**：JWT过期时间，影响用户登录状态的保持时间

### 3.3 最佳实践

1. **环境分离**：
   - 开发环境：使用 `application-dev.properties`
   - 测试环境：使用 `application-test.properties`
   - 生产环境：使用 `application-prod.properties`
   - 通过 `spring.profiles.active` 激活对应环境

2. **敏感信息处理**：
   - 生产环境中避免在配置文件中硬编码密码等敏感信息
   - 使用环境变量或配置中心管理敏感信息

3. **性能优化**：
   - 根据实际负载调整连接池大小
   - 合理设置日志级别，避免过多日志影响性能

4. **安全性**：
   - 生产环境中启用SSL（`useSSL=true`）
   - 使用强密码和密钥
   - 定期更新依赖版本，修复安全漏洞

## 4. 常见问题排查

### 4.1 数据库连接失败

- **检查配置**：确认数据库URL、用户名、密码是否正确
- **检查网络**：确认数据库服务是否运行，网络连接是否正常
- **检查权限**：确认数据库用户是否有相应权限

### 4.2 应用启动失败

- **检查端口**：确认 `server.port` 未被占用
- **检查依赖**：确认所有依赖都已正确下载
- **检查配置**：确认配置文件中没有语法错误

### 4.3 性能问题

- **检查连接池**：调整Hikari连接池配置
- **检查日志**：降低日志级别，减少日志输出
- **检查代码**：优化数据库查询和业务逻辑

## 5. 配置文件版本控制

- **pom.xml**：应纳入版本控制，记录项目依赖变更
- **application.properties**：开发和测试环境配置应纳入版本控制
- **生产环境配置**：建议不纳入版本控制，使用环境变量或配置中心管理

## 6. 总结

项目配置是应用运行的基础，合理的配置可以提高应用的性能、稳定性和安全性。本文档提供了项目配置的详细说明，希望能帮助开发人员更好地理解和管理项目配置。

如需进一步了解Spring Boot配置的详细信息，请参考官方文档：
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Reference Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Reference Documentation](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
