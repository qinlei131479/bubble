# 脚本部署

## 部署脚本

位于 `script/deploy/`：

| 脚本 | 用途 |
|------|------|
| `deploy.sh` | JAR 进程管理 + 健康检查 |
| `deploy_flow.sh` | 工作流服务部署（预留） |

## 使用方式

```bash
# 部署指定服务
bash script/deploy/deploy.sh <service-name> start

# 查看状态
bash script/deploy/deploy.sh <service-name> status

# 停止
bash script/deploy/deploy.sh <service-name> stop
```

## 数据库初始化

```bash
mysql -u root -p < script/db/bubble.sql
mysql -u root -p < script/db/bubble_config.sql
```

`script/db/Dockerfile` 用于构建包含初始化 SQL 的 MySQL 镜像。
