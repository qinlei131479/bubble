# 项目介绍

## Bubble 是什么

Bubble 是面向下一代人机交互的 **智能体原生平台**，融合 LLM、RAG 检索增强与自主智能体技术，以"泡泡"为核心理念，构建轻量化、可组合、自进化的 AI 应用生态。

## 核心技术栈

| 层级 | 技术 | 版本         |
|------|------|------------|
| JDK | Java | 17+        |
| 后端框架 | Spring Boot | 3.5.11     |
| 微服务 | Spring Cloud | 2025.0.1   |
| 微服务(阿里) | Spring Cloud Alibaba | 2025.0.0.0 |
| 认证授权 | Spring Authorization Server | 1.5.2      |
| ORM | MyBatis Plus | 3.5.16     |
| 注册/配置中心 | Nacos | -          |
| 网关 | Spring Cloud Gateway (WebFlux) | -          |
| 前端框架 | Vue 3 + TypeScript | 3.5 / 5.6  |
| UI 组件库 | Element Plus | 2.13       |
| CSS | Tailwind CSS + SCSS | 3.4        |
| 构建工具 | Vite | 5.4.11     |
| 数据库 | MySQL | 8.0        |
| 缓存 | Redis | -          |
| 对象存储 | S3 兼容 (AWS SDK) | -          |

## 当前开发状态

- **已完成**: backend(用户权限)、agi(智能体)、codegen(代码生成)、quartz(定时任务)、auth(认证)、gateway(网关)、monitor(监控)
- **迁移中**: bubble-biz-oa（PHP Laravel 9 → Java，10 阶段计划，详见 [迁移计划](/plans/oa-migration-plan)）
- **占位**: bubble-biz-flow（仅启动类）、agentic（Python 智能体，待实现）
