## 端口与服务清单（单一事实源）

> 本文档用于统一维护“服务 → 端口 → 职责 → 关键依赖”，避免在多个 README/脚本/规则里重复维护。

| 模块 | 端口 | 职责 | 关键依赖 |
|---|---:|---|---|
| bubble-gateway | 8666 / 9999 | Spring Cloud Gateway 统一入口 | Redis, Nacos |
| bubble-auth | 8766 | OAuth2 认证授权中心 | Redis, Nacos, Feign→backend |
| bubble-biz-backend | 8801 | 用户权限管理系统业务 | MySQL, Redis, Nacos, OSS |
| bubble-biz-flow | 8802 | 工作流业务（占位） | MySQL, Redis, Nacos |
| bubble-biz-oa | 8803 | OA 业务（迁移中） | MySQL, Redis, Nacos |
| bubble-biz-agi | 8805 | AGI 智能体业务 | MySQL(多驱动), Redis, Nacos |
| bubble-codegen | 8901 | 代码生成 | MySQL, Redis, Nacos, 动态数据源 |
| bubble-monitor | 8902 | Spring Boot Admin 监控 | Nacos |
| bubble-quartz | 8903 | 定时任务管理 | MySQL, Redis, Nacos |
| Bubble-UI（Docker） | 80 | Nginx → 反代 gateway | bubble-gateway |

### 约定

- 文档内端口以“对外暴露端口”为主；容器内部端口、网关映射等细节，放到 `docs/wiki/ops/docker.md` 统一描述。
