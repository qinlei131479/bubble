## 模块职责与边界

### `apps/bubble-cloud`（后端）

- **定位**：Java 微服务后端聚合工程。
- **内容**：网关/认证/业务服务/公共组件/可视化管理等。
- **文档入口**：`apps/bubble-cloud/README.md`

### `apps/bubble-ui`（前端）

- **定位**：Vue 3 管理端应用。
- **内容**：页面、路由、权限、API 封装、主题等。
- **文档入口**：`apps/bubble-ui/README.md`

### `apps/python`（预留：智能体开发服务）

- **定位**：用于智能体/工具调用/工作流等 Python 侧能力的独立服务或 SDK。
- **约束**：
  - 不与 Java 模块耦合目录结构（仅通过 API/消息/SDK 约定交互）
  - 必须有独立 `README.md`、依赖管理文件、最小可运行入口
