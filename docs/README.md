## docs（VitePress 知识库）

本目录将使用 **VitePress** 搭建项目知识库。当前先落地信息架构（目录骨架 + 约定），后续再接入 `.vitepress/` 配置与侧边栏。

### 目录结构（规划）

```
docs/
├── getting-started/      # 新人/快速上手
├── architecture/         # 架构与模块边界（单一事实源）
├── development/          # 开发指南（后端/前端/Python 智能体）
├── ops/                  # 运维与部署（Docker/Nacos/观测）
├── plans/                # 迁移/规划（保留现有内容）
└── adr/                  # 架构决策记录（为什么这么做）
```

### 写作约定

- **避免重复**：README 只做入口链接；端口/模块关系/部署方式等“经常引用的事实”只在 `docs/architecture/` 维护一份。
- **事实与规范分离**：
  - `docs/`：可发布的知识库（面向读者）
  - `.cursor/rules/`：面向开发与 VibeCoding 的强约束（面向工程与 AI）
