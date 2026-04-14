# 前端开发

## 技术栈

- Vue 3.5 + `<script setup>` + TypeScript（strict 模式）
- Element Plus 2.13 + Tailwind CSS 3.4 + SCSS
- Vite 5.4 构建，路径别名 `/@/*` → `src/*`
- Pinia 状态管理 + vue-router Hash 模式

## 目录职责

```text
src/
├── api/{module}/      → REST API 封装，按后端模块划分
├── components/        → 全局通用组件
├── views/{module}/    → 页面组件
├── stores/            → Pinia store
├── router/            → 路由定义与守卫
├── layout/            → 主布局框架
├── hooks/             → 组合式函数
├── utils/             → 工具函数
├── i18n/              → 语言包
├── theme/             → 全局样式
└── types/             → TypeScript 类型声明
```

## 路由机制

默认使用后端菜单控制路由（`themeConfig.isRequestRoutes: true`），调用 `getAdminMenu()` 获取菜单，`import.meta.glob` 动态映射组件。

## 新增页面清单

1. `src/views/{module}/` 创建页面组件
2. `src/api/{module}/` 创建 API 封装
3. 后端 `sys_menu` 表插入菜单/权限数据
4. 权限按钮使用 `<auth>` 组件或 `v-auth` 指令

## 编码规范

详见 `.cursor/rules/frontend-conventions.mdc`。
