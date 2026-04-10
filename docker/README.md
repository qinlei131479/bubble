## docker（统一容器化模块）

本目录用于统一管理本仓库的容器化部署（基础设施、后端微服务、前端 Nginx 静态服务）。

### 文件说明

- `docker-compose.yml`：基础设施与后端服务编排（MySQL/Redis/Nacos/Gateway/Auth/各 biz/visual）
- `docker-compose-ui.yaml`：前端 Nginx 静态服务编排（依赖后端网关网络）
- `Dockerfile`：前端 Nginx 镜像构建（将 `dist/` 拷贝到容器）
- `bubble-ui-nginx.conf`：Nginx 配置（`/api/*` 反代到 `bubble-gateway:9999`）

### 后端服务清单（docker-compose.yml 覆盖范围）

> 这里只列出 `apps/bubble-cloud` 内被 compose 编排的服务/模块范围，端口映射请以单一事实源为准：
> - `docs/architecture/ports.md`

```
apps/bubble-cloud/
├── bubble-register/            # Nacos（容器镜像构建目录位于 docker-compose 中配置的 context）
├── bubble-gateway/             # 网关
├── bubble-auth/                # 认证
├── bubble-biz/
│   ├── bubble-biz-backend/
│   ├── bubble-biz-oa/
│   ├── bubble-biz-agi/
│   └── bubble-biz-flow/        # 占位（如未启用可从 compose 移除/不启动）
└── bubble-visual/
    ├── bubble-monitor/
    ├── bubble-codegen/
    └── bubble-quartz/
```

### 一键启动（后端 + 基础设施）

在仓库根目录执行：

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

### 启动前端（需要后端网关网络已存在）

`docker/docker-compose-ui.yaml` 使用外部网络 `spring_cloud_default`（由后端 compose 创建）。

```bash
docker compose -f docker/docker-compose-ui.yaml up -d --build
```

### 约定

- `docker/` 仅承载 **Dockerfile/compose/配置**；业务代码统一在 `apps/*`。
- 端口与服务清单以 `docs/architecture/ports.md` 为单一事实源（README 只做入口链接）。
