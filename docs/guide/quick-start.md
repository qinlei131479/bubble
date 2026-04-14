# 快速开始

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK | 17+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| Docker & Compose | 最新稳定版 |
| MySQL | 8.0 |
| Redis | 6+ |

## 方式一：Docker 一键启动

最快的体验方式，在仓库根目录执行：

```bash
docker compose -f docker/docker-compose.yml up -d --build
```

详见 [Docker 部署](/ops/docker)。

## 方式二：本地开发

### 1. 启动基础设施

```bash
# 用 Docker 启动 MySQL + Redis + Nacos
docker compose -f docker/docker-compose.yml up -d mysql redis register
```

### 2. 初始化数据库

```bash
mysql -u root -p < script/db/bubble.sql
mysql -u root -p < script/db/bubble_config.sql
```

### 3. 启动后端

```bash
cd apps/bubble-cloud
mvn clean install -DskipTests
```

按顺序启动：Nacos → Gateway → Auth → biz-backend → 其他服务。

### 4. 启动前端

```bash
cd apps/bubble-ui
npm install
npm run dev
```

### 5. 访问

- 前端：`http://localhost:${VITE_PORT}`
- 网关：`http://localhost:9999`
- Nacos：`http://localhost:8848/nacos`
