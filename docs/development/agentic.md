# 智能体开发

> **状态：规划中** — 本页内容将在 `apps/agentic` 正式启动后补充。

## 定位

Python 侧的智能体运行时服务，负责 Agent 编排、工具调用、RAG 检索增强。

## 技术选型（规划）

- Python 3.11+
- FastAPI / gRPC
- LangChain / LangGraph
- 依赖管理：uv + pyproject.toml
- 代码风格：Ruff + mypy

## 与 Java 侧的交互

- 通过 HTTP API 调用 `bubble-gateway`，不直接依赖 Java 模块
- 共享数据库表仅读取，写入通过 Java API
- 异步任务通过 Redis Pub/Sub 或消息队列

## 编码规范

详见 `.cursor/rules/python-conventions.mdc`。
