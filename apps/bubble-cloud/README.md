## Bubble-Cloud（Java 微服务后端）

Spring Cloud 2025 + Spring Boot 3.5 + MyBatis-Plus 聚合工程。

### 模块概览

```text
bubble-cloud/
├── bubble-gateway/          # Spring Cloud Gateway 网关 (:8666)
├── bubble-auth/             # OAuth2 认证中心 (:8766)
├── bubble-api/              # 公共 API（实体/DTO/Feign，jar 库）
│   ├── bubble-api-backend
│   ├── bubble-api-agi
│   ├── bubble-api-oa
│   └── bubble-api-flow
├── bubble-biz/              # 业务服务（可独立部署）
│   ├── bubble-biz-backend   # 用户权限 (:8801)
│   ├── bubble-biz-agi       # AI 智能体 (:8805)
│   ├── bubble-biz-oa        # OA 办公（迁移中）(:8803)
│   └── bubble-biz-flow      # 工作流（占位）(:8802)
├── bubble-common/           # 跨模块公共组件（各子模块为 jar，供业务引用）
│   ├── bubble-common-bom         # Maven BOM，统一依赖版本
│   ├── bubble-common-core        # 公共工具类与核心包
│   ├── bubble-common-datasource  # 动态数据源切换
│   ├── bubble-common-log         # 日志服务
│   ├── bubble-common-mybatis     # MyBatis 封装
│   ├── bubble-common-oss         # 对象存储（OSS）集成
│   ├── bubble-common-seata       # Seata 分布式事务
│   ├── bubble-common-security    # 安全工具类与相关能力
│   ├── bubble-common-feign       # Feign + Sentinel 降级、熔断、限流
│   ├── bubble-common-swagger     # OpenAPI / Swagger 接口文档
│   ├── bubble-common-websocket   # WebSocket
│   ├── bubble-common-xss         # XSS 安全过滤（基于 JSoup）
│   └── bubble-common-excel       # Excel 导入导出
└── bubble-visual/           # 可视化管理
    ├── bubble-codegen       # 代码生成 (:8901)
    ├── bubble-monitor       # Spring Boot Admin (:8902)
    └── bubble-quartz        # 定时任务 (:8903)
```

### 环境要求

- **JDK**: 17+
- **Maven**: 3.9+
- **基础设施**: MySQL 8.0、Redis、Nacos

### 本地开发

1. 启动基础设施（任选其一）：
   - Docker 方式：`docker compose -f docker/docker-compose.yml up mysql redis register -d`
   - 本地安装：自行启动 MySQL(3306)、Redis(6379)、Nacos(8848)

2. 导入数据库：
   ```bash
   mysql -u root -p < script/db/bubble.sql
   mysql -u root -p < script/db/bubble_config.sql
   ```

3. Maven 构建：
   ```bash
   cd apps/bubble-cloud
   mvn clean install -DskipTests
   ```

4. 按顺序启动服务：Nacos → Gateway → Auth → biz-backend → 其他

### 关键约束

- 开发规范：`.cursor/rules/backend-conventions.mdc`
- 端口清单：[`docs/wiki/architecture/ports.md`](../../docs/wiki/architecture/ports.md)（单一事实源）
- OA 迁移计划：[`docs/wiki/plans/oa-migration-plan.md`](../../docs/wiki/plans/oa-migration-plan.md)
