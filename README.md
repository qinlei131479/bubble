## 项目概述

**Bubble** 是一个面向下一代人机交互的**智能体原生平台/微服务工程**，融合 LLM、RAG 检索增强与自主智能体能力，以“泡泡”为核心理念，构建轻量化、可组合、自进化的 AI 应用生态。

本仓库采用 **Monorepo** 目录组织，以便更好兼容 VibeCoding（让目录结构本身就能表达“是什么/在哪里/怎么跑”）。

## 快速入口（可点击）

- **后端（Java 微服务）**：[apps/bubble-cloud/README.md](apps/bubble-cloud/README.md)
- **前端（Vue3）**：[apps/bubble-ui/README.md](apps/bubble-ui/README.md)
- **Python 智能体服务（预留）**：[apps/python/README.md](apps/python/README.md)
- **Docker 一键部署**：[docker/README.md](docker/README.md)
- **知识库（VitePress）**：[docs/README.md](docs/README.md)
- **开发规范（Cursor Rules）**：[`./.cursor/rules/`](.cursor/rules/)
  - 总览：[.cursor/rules/project-overview.mdc](.cursor/rules/project-overview.mdc)
  - 架构与端口：[.cursor/rules/module-architecture.mdc](.cursor/rules/module-architecture.mdc)
  - Monorepo 接入规范：[.cursor/rules/monorepo-layout.mdc](.cursor/rules/monorepo-layout.mdc)

## 目录结构（Monorepo 总览）

```
Bubble/
├── apps/
│   ├── bubble-cloud/          # Java 微服务后端（聚合工程）
│   ├── bubble-ui/             # Vue3 前端应用
│   └── python/                # 预留：智能体开发服务（Python，后续新增）
├── docker/                    # 统一容器化：compose、nginx 配置等
├── docs/                      # 文档知识库（将使用 VitePress）
├── script/                    # 脚本与资产（部署脚本、数据库初始化等）
└── .cursor/rules/             # 面向 VibeCoding 的开发规范与约束
```

## 文档与事实源（避免重复）

- **端口与服务清单（单一事实源）**：[`docs/architecture/ports.md`](docs/architecture/ports.md)
- **模块职责与边界**：[`docs/architecture/modules.md`](docs/architecture/modules.md)
- **OA 迁移计划**：[`docs/plans/oa-migration-plan.md`](docs/plans/oa-migration-plan.md)

## 分支说明

- `JDK17_master`: Java 17 + Spring Boot 3.5 + Spring Cloud 2025
- `JDK8_master`: Java 8 + Spring Boot 2.7 + Spring Cloud 2021