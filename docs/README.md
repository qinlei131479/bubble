# Bubble 知识库

本目录使用 [VitePress](https://vitepress.dev/) 搭建项目知识库。正文 Markdown 位于 `wiki/`，通过 `.vitepress/config.ts` 中的 `rewrites` 将 `wiki/index.md` 映射为站点根路径 `/`，便于在仓库内用统一前缀组织文档。

## 目录结构

```text
docs/
├── .vitepress/
│   └── config.ts              # 导航、侧边栏、rewrites（首页映射等）
├── package.json               # vitepress 脚本与依赖
├── README.md                  # 本说明（仓库内入口）
└── wiki/
    ├── index.md               # 站点首页（rewrites → /）
    ├── guide/                 # 入门指南（介绍、快速开始、目录结构）
    ├── architecture/          # 架构设计（总览、端口、模块、数据库、测试策略）
    ├── development/           # 开发指南（后端、前端、智能体）
    ├── ops/                   # 运维部署（Docker、脚本、监控）
    ├── changelog/             # 版本变更日志
    ├── plans/                 # 迁移与规划（含 OA 10 阶段计划）
    └── reference/             # 参考手册（环境变量、错误码、API 约定）
```

## 文档生命周期

```text
需求/规划 → 架构设计 → 开发指南 → 测试策略 → 运维部署 → 版本迭代 → 参考手册
wiki/plans/  wiki/architecture/  wiki/development/  wiki/architecture/strategy.md  wiki/ops/  wiki/changelog/  wiki/reference/
```

（测试策略文档为 `wiki/architecture/strategy.md`，在侧边栏中归入「架构设计 → 测试」。）

## 本地预览

```bash
cd docs
npm install   # 首次
npm run dev   # 或 npx vitepress dev --port 5176（见 package.json）
```

## 写作约定

- **避免重复**：本 README 只做入口说明；端口/模块等事实只在 `wiki/architecture/` 等处维护一份
- **事实与规范分离**：`docs/wiki/` 面向读者的知识库；`.cursor/rules/` 面向 AI 的开发约束
