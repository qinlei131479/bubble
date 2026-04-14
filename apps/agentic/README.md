## Bubble-Agentic（Python 智能体服务）

> **状态：规划中** — 当前仅结构占位，代码尚未引入。

### 定位

Python 侧的智能体运行时服务，负责：

- Agent 编排与工具调用（LangChain / LangGraph / 自研）
- RAG 检索增强管道
- 与 `bubble-biz-agi`（Java）的 API / 消息协作

### 技术选型（规划）

- Python 3.11+
- FastAPI / gRPC
- LangChain / LangGraph
- 依赖管理：pyproject.toml（uv / poetry）

### 接入时需完成

1. `pyproject.toml` + `src/` 入口
2. `.env.example` 环境变量示例
3. `Dockerfile`（纳入 `docker/docker-compose.yml`）
4. 更新 [`docs/architecture/modules.md`](../../docs/wiki/architecture/modules.md)

### 边界约定

- 不与 Java 模块耦合目录结构，仅通过 HTTP API / 消息队列交互
- 数据持久化优先复用 Java 侧 MySQL，避免独立建库
