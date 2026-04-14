<p align="center" style="font-family: '楷体'; font-size: 30px;">智能如泡，聚变未来</p>

## Bubble 是什么

Bubble 是面向下一代人机交互的 **智能体原生平台**，融合 LLM、RAG 检索增强与自主智能体技术，
以"泡泡"为核心理念，构建轻量化、可组合、自进化的 AI 应用生态。

## 仓库结构

```text
Bubble/                          # Monorepo 根
├── apps/
│   ├── bubble-cloud/            # Java 微服务后端（Spring Cloud 2025）
│   ├── bubble-ui/               # Vue 3 管理端前端
│   └── agentic/                 # Python 智能体服务（规划中）
├── docker/                      # 容器编排与部署配置
├── docs/                        # VitePress 知识库
├── script/                      # 脚本（数据库初始化、部署）
└── .cursor/rules/               # AI 开发规范（VibeCoding）
```

## 快速开始

| 我想…… | 去哪里 |
|--------|--------|
| 启动后端微服务 | [`apps/bubble-cloud/README.md`](apps/bubble-cloud/README.md) |
| 启动前端开发 | [`apps/bubble-ui/README.md`](apps/bubble-ui/README.md) |
| Docker 一键部署 | [`docker/README.md`](docker/README.md) |
| 了解智能体服务规划 | [`apps/agentic/README.md`](apps/agentic/README.md) |
| 查阅完整文档 | [`docs/`](docs/) （VitePress 知识库） |

## 分支策略

| 分支 | JDK | Spring Boot | 状态 |
|------|-----|-------------|------|
| `JDK17_master` | 17+ | 3.5.x | **主力开发** |
| `JDK8_master` | 8 | 2.7.x | 兼容维护 |

