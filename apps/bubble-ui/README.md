<p align="center" style="font-family: '楷体'; font-size: 30px;">智能如泡，聚变未来</p>

<p align="center">
 <img src="https://img.shields.io/badge/Bubble-UI-0.1-success.svg" alt="Build Status">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2025-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.5-blue.svg" alt="Downloads">

</p>

## 项目概述

`bubble-ui` 是本仓库的 **Vue 3 前端应用**，用于对接 `bubble-cloud` 微服务后端并提供管理端界面。

如需查看仓库总览与目录导航，请回到根目录 `README.md`。

## 功能特性

- **Vue 3**: 利用最新版本的 Vue.js 实现现代化的响应式体验。
- **Element Plus**: 集成了 Element Plus，提供丰富的 UI 组件。
- **Vite**: 使用 Vite 进行快速构建和模块热替换。
- **TypeScript**: 支持 TypeScript，提升代码质量和可维护性。
- **Tailwind CSS**: 使用 Tailwind CSS 进行样式设计。

## 快速开始

### 先决条件

- **Node.js**: 版本 18.0.0。
- **npm**: 版本 8.0.0 或更高。

### 本地启动

```bash
npm install
npm run dev
```

### 与后端联调

- 后端工程位于 `apps/bubble-cloud/`（详见其 `README.md`）。
- 如使用 Docker 方式启动后端与基础设施，参考 `docker/README.md`。
  - 默认由 Nginx 反向代理到网关（`/api/*` → `bubble-gateway`）。

## 浏览器支持

- 现代浏览器的最后两个版本。
- 不支持 IE 11 及更低版本。