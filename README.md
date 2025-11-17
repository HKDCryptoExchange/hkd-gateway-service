# HKD API Gateway Service

HKD加密货币交易所的API网关服务，基于Spring Cloud Gateway实现。

## 功能特性

### 核心功能
- ✅ **路由转发** - 统一入口，动态路由到各个微服务
- ✅ **JWT认证** - 基于JWT Token的统一身份验证
- ✅ **多维度限流** - IP限流、用户限流、API限流（令牌桶算法）
- ✅ **熔断降级** - Resilience4j熔断器，服务不可用时优雅降级
- ✅ **跨域支持** - CORS配置，支持前端跨域访问
- ✅ **访问日志** - 记录所有请求的详细日志
- ✅ **服务发现** - 集成Nacos，自动发现后端服务
- ✅ **监控指标** - Prometheus指标暴露，支持监控告警

### 过滤器链
```
请求流程：
  ↓
[JwtAuthenticationFilter] (-100) - JWT验证，提取用户信息
  ↓
[RateLimitFilter] (-90) - 多维度限流
  ↓
[其他自定义过滤器]
  ↓
[路由转发]
  ↓
[熔断降级] - 后端服务不可用时降级
  ↓
[AccessLogFilter] (LOWEST) - 访问日志记录
```

## 技术栈

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Cloud Gateway 2023.0.0**
- **Spring Cloud Alibaba (Nacos)**
- **Resilience4j** - 熔断降级
- **Redis** - 限流、缓存
- **JJWT** - JWT Token处理
- **Prometheus** - 监控指标

## 项目结构

```
hkd-gateway-service/
├── src/main/java/com/hkd/gateway/
│   ├── GatewayApplication.java          # 启动类
│   ├── config/
│   │   ├── CorsConfig.java              # CORS配置
│   │   └── CircuitBreakerConfig.java    # 熔断器配置
│   ├── filter/
│   │   ├── JwtAuthenticationFilter.java # JWT验证过滤器
│   │   ├── RateLimitFilter.java         # 限流过滤器
│   │   └── AccessLogFilter.java         # 访问日志过滤器
│   ├── service/
│   │   ├── JwtService.java              # JWT服务
│   │   └── TokenBucketRateLimiter.java  # 令牌桶限流器
│   ├── controller/
│   │   └── FallbackController.java      # 降级处理器
│   └── exception/
│       └── AuthException.java           # 认证异常
└── src/main/resources/
    ├── application.yml                  # 主配置文件
    ├── application-dev.yml              # 开发环境配置
    └── application-prod.yml             # 生产环境配置
```

## 配置说明

### 环境变量

| 环境变量 | 说明 | 默认值 |
|---------|------|--------|
| `SERVER_PORT` | 服务端口 | 8000 |
| `NACOS_SERVER` | Nacos服务地址 | localhost:8848 |
| `REDIS_HOST` | Redis主机 | localhost |
| `REDIS_PORT` | Redis端口 | 6379 |
| `REDIS_PASSWORD` | Redis密码 | hkd_redis_2024 |
| `JWT_SECRET` | JWT密钥 | *请生产环境修改* |

### 路由配置

网关已配置以下服务路由：

| 路径前缀 | 目标服务 | 说明 |
|---------|---------|------|
| `/api/v1/users/**` | hkd-user-service | 用户服务 |
| `/api/v1/auth/**` | hkd-auth-service | 认证服务 |
| `/api/v1/kyc/**` | hkd-kyc-service | KYC服务 |
| `/api/v1/accounts/**` | hkd-account-service | 账户服务 |
| `/api/v1/wallets/**` | hkd-wallet-service | 钱包服务 |
| `/api/v1/deposits/**` | hkd-deposit-service | 充值服务 |
| `/api/v1/withdrawals/**` | hkd-withdraw-service | 提现服务 |
| `/api/v1/assets/**` | hkd-asset-service | 资产服务 |
| `/api/v1/orders/**` | hkd-order-gateway | 订单网关 |
| `/api/v1/matching/**` | matching-engine | 撮合引擎 |
| `/api/v1/settlements/**` | hkd-settlement-service | 清算服务 |
| `/api/v1/market/**` | hkd-market-service | 行情服务（HTTP） |
| `/ws/market/**` | hkd-market-service | 行情服务（WebSocket） |
| `/api/v1/risk/**` | hkd-risk-service | 风控服务 |
| `/api/v1/notifications/**` | hkd-notify-service | 通知服务 |
| `/api/v1/admin/**` | hkd-admin-service | 管理后台服务 |

### JWT白名单

以下路径无需JWT验证：
- `/api/v1/auth/**` - 认证接口（登录、注册）
- `/api/v1/market/public/**` - 公开行情接口
- `/actuator/**` - 监控端点
- `/health` - 健康检查
- `/metrics` - 指标端点

### 限流配置

| 限流维度 | 容量 | 补充速率 | 说明 |
|---------|------|---------|------|
| IP限流 | 100 | 100/秒 | 防止单IP恶意请求 |
| 用户限流 | 10 | 10/秒 | 普通用户限流 |
| 交易API限流 | 5 | 5/秒 | 交易接口严格限流 |
| 行情API限流 | 20 | 20/秒 | 行情接口较宽松限流 |
| 默认API限流 | 10 | 10/秒 | 其他接口默认限流 |

## 本地开发

### 前置要求

- JDK 21+
- Maven 3.8+
- Docker (用于Redis和Nacos)

### 启动依赖服务

```bash
# 启动Redis
docker run -d --name hkd-redis \
  -p 6379:6379 \
  redis:7-alpine \
  redis-server --requirepass hkd_redis_2024

# 启动Nacos
docker run -d --name hkd-nacos \
  -e MODE=standalone \
  -p 8848:8848 \
  -p 9848:9848 \
  nacos/nacos-server:v2.3.0
```

### 编译运行

```bash
# 编译
mvn clean package

# 运行（开发环境）
java -jar target/hkd-gateway-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev

# 或使用Maven直接运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 访问端点

- 网关地址: `http://localhost:8000`
- 健康检查: `http://localhost:8000/actuator/health`
- Prometheus指标: `http://localhost:8000/actuator/prometheus`
- 网关路由信息: `http://localhost:8000/actuator/gateway/routes`

## 测试

```bash
# 运行所有测试
mvn test

# 测试JWT验证
curl -H "Authorization: Bearer your_jwt_token" \
  http://localhost:8000/api/v1/users/profile

# 测试限流
for i in {1..15}; do curl http://localhost:8000/api/v1/market/ticker; done
```

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t hkd/gateway-service:1.0.0 .

# 运行容器
docker run -d \
  --name hkd-gateway \
  -p 8000:8000 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e NACOS_SERVER=nacos:8848 \
  -e REDIS_HOST=redis \
  -e REDIS_PASSWORD=your_redis_password \
  -e JWT_SECRET=your_jwt_secret \
  hkd/gateway-service:1.0.0
```

### Kubernetes部署

参考 `k8s/gateway-deployment.yaml`

## 监控

### Prometheus指标

```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'hkd-gateway'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8000']
```

### 关键指标

- `http_server_requests_seconds` - 请求响应时间
- `gateway_requests_total` - 总请求数
- `gateway_requests_rate_limited` - 限流拦截数
- `resilience4j_circuitbreaker_state` - 熔断器状态

## 性能指标

- **吞吐量**: 10,000+ QPS
- **P99延迟**: < 10ms（网关本身）
- **可用性**: 99.9%+

## 安全性

- ✅ JWT Token验证
- ✅ 多维度限流防护
- ✅ CORS跨域配置
- ✅ Token黑名单（支持强制登出）
- ⚠️ 生产环境必须修改JWT_SECRET

## 常见问题

### Q: JWT验证失败？
A: 检查Token格式是否正确（Bearer prefix）、JWT_SECRET配置是否一致、Token是否过期。

### Q: 限流频繁触发？
A: 调整`application.yml`中的限流配置，或检查是否有批量请求。

### Q: 后端服务无法访问？
A: 检查Nacos服务发现是否正常，后端服务是否已注册到Nacos。

## 贡献指南

请参考：[CONTRIBUTING.md](../CONTRIBUTING.md)

## 许可证

[MIT License](LICENSE)

---

**HKD Exchange** - Built with ❤️ by HKD Team
