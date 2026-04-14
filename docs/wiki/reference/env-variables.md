# 环境变量

## 后端（Nacos / Spring Boot）

各服务通过 Nacos 配置中心管理，关键环境变量：

| 变量 | 用途 | 使用方 |
|------|------|--------|
| `MYSQL_HOST` | MySQL 地址 | 所有业务服务 |
| `REDIS_HOST` | Redis 地址 | 所有服务 |
| `NACOS_HOST` | Nacos 注册中心地址 | 所有服务 |

## 前端（Vite）

| 变量 | 用途 | 默认值 |
|------|------|--------|
| `VITE_API_URL` | API 基础地址 | - |
| `VITE_ADMIN_PROXY_PATH` | 开发代理目标 | - |
| `VITE_PORT` | 开发服务器端口 | - |
| `VITE_PUBLIC_PATH` | 生产 base path | `/` |
| `VITE_IS_MICRO` | 是否微服务模式 | - |
| `VITE_PWD_ENC_KEY` | 密码加密密钥 | - |
| `VITE_OAUTH2_PASSWORD_CLIENT` | OAuth2 密码客户端 | - |
| `VITE_OAUTH2_MOBILE_CLIENT` | OAuth2 手机客户端 | - |

## Docker Compose

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_PORT` | 33306 | MySQL 对外端口 |
| `MYSQL_ROOT_PASSWORD` | root | MySQL root 密码 |
| `REDIS_PORT` | 36379 | Redis 对外端口 |
