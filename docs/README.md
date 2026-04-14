# Bubble 知识库

本目录使用 [VitePress](https://vitepress.dev/) 搭建项目知识库。

## 目录结构

```text
docs/
├── .vitepress/config.ts     # VitePress 配置（导航、侧边栏）
├── index.md                 # 首页
├── guide/                   # 入门指南（介绍、快速开始、目录结构）
├── architecture/            # 架构设计（总览、端口、模块、数据库）
├── development/             # 开发指南（后端、前端、智能体）
├── testing/                 # 测试策略
├── ops/                     # 运维部署（Docker、脚本、监控）
├── changelog/               # 版本变更日志
├── plans/                   # 迁移与规划（OA 10 阶段计划）
├── reference/               # 参考手册（环境变量、错误码、API 约定）
└── public/images/           # 静态资源
```

## 文档生命周期

```
需求/规划 → 架构设计 → 开发指南 → 测试策略 → 运维部署 → 版本迭代 → 参考手册
 plans/    architecture/  development/  testing/    ops/      changelog/   reference/
```

## 本地预览

```bash
cd docs
npx vitepress dev
```

## 写作约定

- **避免重复**：README 只做入口链接；端口/模块等事实只在 `architecture/` 维护一份
- **事实与规范分离**：`docs/` 面向读者的知识库；`.cursor/rules/` 面向 AI 的开发约束
