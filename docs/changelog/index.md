# 变更日志

> 遵循 [Keep a Changelog](https://keepachangelog.com/zh-CN/) 格式，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [3.9.2] - 2026-04-14

### Monorepo 改造

- 仓库目录重构为 `apps/ + docker/ + docs/ + script/` Monorepo 结构
- Docker 前后端 compose 合并为统一 `docker-compose.yml`
- `.cursor/rules/` 重构：精简 alwaysApply 规则，新增 `project-context.mdc`
- VitePress 知识库骨架搭建

### 后端

- OA 迁移持续推进中（PHP Laravel 9 → Java Spring Boot 3）
- 当前进度：80 Controller / ~300 Endpoint，约 45% 真实逻辑

### 前端

- Vue 3.5 + Element Plus 2.13 稳定运行

---

## [3.9.1] - 2026-03-30

### 新增

- OA 迁移主计划 v2.0 发布（10 阶段拆分）
- `bubble-biz-agi` AI 智能体业务模块

### 变更

- Spring Cloud 升级至 2025.0.1
- Spring Cloud Alibaba 升级至 2025.0.0.0

---

## 版本规划

| 版本 | 计划内容 | 预估时间 |
|------|----------|----------|
| 3.10.0 | OA 迁移阶段 1-2 完成（用户组织 + 系统配置） | 2026 Q2 |
| 3.11.0 | OA 迁移阶段 3-4 完成（HR + OA 工作流） | 2026 Q3 |
| 4.0.0 | Python 智能体服务上线 | 2026 Q4 |

---

_更早的变更记录待整理补充。_
