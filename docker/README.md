## docker（统一容器化模块）

本目录统一管理 Bubble 的容器化部署：基础设施、后端微服务、前端 Nginx。

### 文件说明

```text
docker/
├── docker-compose.yml       # 全栈编排（基础设施 + 后端 + 前端）
├── bubble-ui.Dockerfile     # 前端 Nginx 镜像构建
├── nginx/
│   └── default.conf         # Nginx 配置（/api/* 反代到 gateway）
├── .env.example             # compose 环境变量示例（可选）
└── README.md
```

### 一键启动

```bash
# 全部服务
docker compose -f docker/docker-compose.yml up -d --build

# 仅基础设施（开发时常用）
docker compose -f docker/docker-compose.yml up -d mysql redis register

# 仅前端 + 网关
docker compose -f docker/docker-compose.yml up -d gateway ui
```

### 环境变量（可选覆盖）

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `MYSQL_PORT` | 33306 | MySQL 对外端口 |
| `MYSQL_ROOT_PASSWORD` | root | MySQL root 密码 |
| `REDIS_PORT` | 36379 | Redis 对外端口 |

在 `docker/` 目录下创建 `.env` 文件即可覆盖默认值。

### 约定

- `docker/` 仅承载 Dockerfile / compose / 配置，业务代码统一在 `apps/`
- 端口与服务清单以 [`docs/architecture/ports.md`](../docs/wiki/architecture/ports.md) 为单一事实源
- 网络名统一为 `bubble-net`
