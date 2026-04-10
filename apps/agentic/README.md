## apps/python（预留：智能体开发服务）

本目录预留给 Python 侧的智能体开发服务（Agent runtime / 工具调用 / 任务编排等）。当前仅提供结构占位，避免后续引入时破坏仓库规范与 ignore 规则。

### 接入要求（最小集合）

- `README.md`：说明用途、启动方式、依赖、与后端/前端的交互边界
- 依赖管理：`pyproject.toml`（推荐）或 `requirements.txt`
- 环境变量示例：`.env.example`（如需要）
- 运行入口：如 `src/` + `main.py` 或 `app/`（按技术选型确定）

> 新增时请同步更新 `docs/architecture/modules.md` 与 `.cursor/rules/monorepo-layout.mdc` 中的接入清单。
