# 日志系统配置说明

## 日志文件存储位置

```
d:\JavaProject\demo\src\main\java\com\example\demo\log\
```

## 日志文件分类

系统会自动生成以下日志文件（按天滚动）：

| 文件名 | 说明 | 日志级别 | 保留天数 | 大小限制 |
|--------|------|----------|----------|----------|
| `pharmacy-inventory-info.log` | 信息日志 | INFO | 30 天 | 包含在总限制中 |
| `pharmacy-inventory-warn.log` | 警告日志 | WARN | 30 天 | 5GB |
| `pharmacy-inventory-error.log` | 错误日志 | ERROR | 30 天 | 5GB |
| `pharmacy-inventory-debug.log` | 调试日志 | DEBUG | 15 天 | 20GB |
| `pharmacy-inventory-all.log` | 完整日志 | ALL | 30 天 | 50GB |
| `pharmacy-inventory-sql.log` | SQL 日志 | SQL | 15 天 | 10GB |
| `pharmacy-inventory-business.log` | 业务日志 | BUSINESS | 30 天 | 10GB |

## 日志滚动策略

- **滚动方式**: 按天滚动
- **文件命名**: `{日志名}-{yyyy-MM-dd}.log`
- **示例**: `pharmacy-inventory-error-2026-03-02.log`

## 配置说明

### 日志级别说明

1. **ERROR**: 错误日志，记录系统错误和异常
2. **WARN**: 警告日志，记录潜在问题
3. **INFO**: 信息日志，记录系统运行状态
4. **DEBUG**: 调试日志，记录详细调试信息
5. **TRACE**: 跟踪日志，最详细的日志（用于 SQL 参数绑定）

### 日志记录器配置

#### 应用日志
- **包**: `com.example.demo`
- **级别**: DEBUG
- **输出**: 控制台 + 所有文件

#### Controller 层
- **包**: `com.example.demo.controller`
- **级别**: DEBUG
- **特色**: 额外输出到业务日志

#### Service 层
- **包**: `com.example.demo.service`
- **级别**: DEBUG
- **特色**: 额外输出到业务日志

#### Repository 层
- **包**: `com.example.demo.repository`
- **级别**: DEBUG
- **特色**: 额外输出到 SQL 日志

#### SQL 日志
- **包**: `org.hibernate.SQL`
- **级别**: DEBUG
- **输出**: SQL 日志文件 + DEBUG 日志

