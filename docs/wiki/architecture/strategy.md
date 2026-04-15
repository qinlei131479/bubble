# 测试策略

> 本文档规划测试体系，将随项目发展逐步完善。

## 测试金字塔

| 层级 | 范围 | 工具 | 现状 |
|------|------|------|------|
| 单元测试 | Service / 工具类 | JUnit 5 + Mockito | 部分覆盖 |
| 集成测试 | Controller + DB | Spring Boot Test + Testcontainers | 规划中 |
| API 对比测试 | PHP vs Java 接口 | 脚本对比响应结构 | OA 迁移验收用 |
| E2E 测试 | 前端页面流程 | Playwright / Cypress | 规划中 |

## OA 迁移验收标准

每阶段完成后需通过：

1. PHP 与 Java 同路径抽样请求对比（路径、关键字段、HTTP 状态、`data` 结构）
2. 前端对应页面可正常操作（登录 → 导航 → CRUD → 关联功能）
3. 该阶段范围内的所有占位桩已被真实逻辑替换

详见 [OA 迁移计划](/docs/wiki/plans/oa-migration-plan)。

## 后续规划

- [ ] 建立 CI 流水线自动执行单元测试
- [ ] 引入 Testcontainers 替代手动数据库准备
- [ ] 前端关键流程 E2E 覆盖
