<p align="center" style="font-family: '楷体'; font-size: 30px;">智能如泡，聚变未来</p>

<p align="center">
  <img src="https://img.shields.io/badge/Vue-3.5-blue" alt="Vue 3">
  <img src="https://img.shields.io/badge/Element%20Plus-2.13-blue" alt="Element Plus">
  <img src="https://img.shields.io/badge/Vite-5.4-purple" alt="Vite">
  <img src="https://img.shields.io/badge/TypeScript-5.6-blue" alt="TypeScript">
</p>

## Bubble-UI（Vue 3 管理端）

基于 Vue 3 + Element Plus + Vite + TypeScript 的管理后台前端。

### 环境要求

- **Node.js**: >= 18.0
- **npm**: >= 8.0（或 pnpm）

### 本地开发

```bash
cd apps/bubble-ui
npm install
npm run dev
```

默认开发服务器端口由 `VITE_PORT` 控制，API 代理到后端网关。

### 与后端联调

- 确保后端 Gateway 已启动（默认 `:9999`）
- `.env.development` 配置 `VITE_API_URL` 指向网关地址
- Docker 部署时 Nginx 自动反代 `/api/*` → `bubble-gateway:9999`

### 关键环境变量

| 变量 | 用途 |
|------|------|
| `VITE_API_URL` | API 基础地址 |
| `VITE_PORT` | 开发服务器端口 |
| `VITE_IS_MICRO` | 是否微服务模式 |
| `VITE_PWD_ENC_KEY` | 密码加密密钥 |

### 浏览器支持

现代浏览器最近两个版本，不支持 IE。

### 关键约束

- 开发规范：`.cursor/rules/frontend-conventions.mdc`
