# 架构总览

## 系统全景

```text
                          ┌─────────────┐
                          │  Browser    │
                          └──────┬──────┘
                                 │
                          ┌──────▼──────┐
                          │ Nginx (:80) │  ← bubble-ui 前端静态资源
                          └──────┬──────┘
                                 │ /api/*
                          ┌──────▼──────────┐
                          │ Gateway (:9999) │  ← Spring Cloud Gateway
                          └──────┬──────────┘
                                 │ 路由转发
               ┌─────────────────┼─────────────────┐
               │                 │                  │
        ┌──────▼──────┐  ┌──────▼──────┐  ┌───────▼───────┐
        │ Auth (:8766)│  │ biz-* 业务   │  │ visual-* 管理  │
        └─────────────┘  └──────┬──────┘  └───────────────┘
                                │ Feign
                         ┌──────▼──────┐
                         │ api-* jar库  │  ← 实体/DTO/RemoteService
                         └─────────────┘

        所有服务 ──→ Nacos (注册中心 + 配置中心)
        所有服务 ──→ MySQL / Redis
```

## 后端模块分层

| 层级 | 命名规则 | 说明 |
|------|----------|------|
| 网关 | `bubble-gateway` | 统一入口、路由、限流 |
| 认证 | `bubble-auth` | OAuth2 认证授权 |
| API | `bubble-api-{module}` | 实体、DTO/VO、Feign 接口（jar 库） |
| 业务 | `bubble-biz-{module}` | Controller / Service / Mapper（可独立部署） |
| 公共 | `bubble-common-{feature}` | 跨模块组件 |
| 管理 | `bubble-visual-{feature}` | 可视化管理服务 |

## 服务清单

详见 [端口与服务](/architecture/ports)、[模块职责](/architecture/modules)。

## 部署架构

- **容器化**：`docker/docker-compose.yml` 统一编排
- **脚本部署**：`script/deploy/deploy.sh`（JAR 进程管理 + 健康检查）
- **网络**：所有容器共享 `bubble-net` bridge 网络
