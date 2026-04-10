## Bubble-Cloud（后端聚合工程）

本目录为 **Java 微服务后端（聚合工程）**，承载所有后端模块（API/业务服务/公共组件/可视化服务等）。

如需查看仓库总览与目录导航，请回到根目录 `README.md`。

## 模块说明

```
apps/bubble-cloud
├── bubble-api/                 # 公共 API（实体、DTO/VO、Feign 等 jar）
├── bubble-auth/                # 认证授权中心
├── bubble-biz/                 # 业务服务（backend/oa/agi/flow…）
├── bubble-common/              # 跨模块公共组件（security/feign/oss…）
├── bubble-gateway/             # Spring Cloud Gateway 网关
├── bubble-visual/              # 可视化管理（codegen/monitor/quartz…）
└── pom.xml                     # Maven 聚合根
```

## 端口与模块依赖

端口映射与模块依赖关系属于“单一事实源”，请以 `docs/architecture/ports.md`（将迁入 VitePress）为准；开发时也可参考 `.cursor/rules/module-architecture.mdc`。

## 本地开发（概览）

> 这里仅保留入口说明，避免与根 README/部署文档重复；详细步骤建议统一沉淀到 `docs/getting-started/`。

- **JDK**：17+
- **构建工具**：Maven（聚合 `pom.xml`）
- **基础依赖**：MySQL、Redis、Nacos（可使用 `docker/README.md` 提供的方式启动）

## 相关文档入口

- 开发规范：`.cursor/rules/backend-conventions.mdc`
- 部署（Docker）：`docker/README.md`
- OA 迁移计划：`docs/plans/oa-migration-plan.md`