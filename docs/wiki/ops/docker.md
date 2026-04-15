# Docker 部署

## 编排文件

所有服务通过 `docker/docker-compose.yml` 统一编排，包括：

- **基础设施**：MySQL、Redis、Nacos
- **后端微服务**：Gateway、Auth、biz-backend、monitor、codegen、quartz
- **前端**：Nginx 静态服务

## 快速启动

```bash
# 全部服务
docker compose -f docker/docker-compose.yml up -d --build

# 仅基础设施（开发时常用）
docker compose -f docker/docker-compose.yml up -d mysql redis register

# 仅前端 + 网关
docker compose -f docker/docker-compose.yml up -d gateway ui
```

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_PORT` | 33306 | MySQL 对外端口 |
| `MYSQL_ROOT_PASSWORD` | root | MySQL root 密码 |
| `REDIS_PORT` | 36379 | Redis 对外端口 |

在 `docker/` 下创建 `.env` 文件覆盖默认值，参考 `docker/.env.example`。

## 网络

所有容器共享 `bubble-net` bridge 网络，服务间通过容器名互访。

## 前端 Nginx

- 构建文件：`docker/bubble-ui.Dockerfile`
- 配置：`docker/nginx/default.conf`
- `/api/*` 请求反代到 `bubble-gateway:8666`

## 注意事项

- 业务代码在 `apps/`，`docker/` 仅放编排和配置
- 端口清单见 [端口与服务](/architecture/ports)
