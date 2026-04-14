# 监控与告警

## Spring Boot Admin

- 服务：`bubble-monitor`（端口 5001）
- 功能：实例健康、JVM 指标、日志级别动态调整、环境属性查看

访问地址：`http://<host>:5001`

## 日志

- 框架：Logback（`logback-spring.xml`）
- 日志目录：各服务的 `logs/`
- 操作日志：`@SysLog` 注解 → 异步写入 `sys_log` 表

## Nacos 控制台

- 地址：`http://<host>:8848/nacos`
- 功能：服务注册列表、配置管理、命名空间

## 后续规划

- [ ] 接入 Prometheus + Grafana 指标监控
- [ ] 接入日志聚合（ELK / Loki）
- [ ] 告警通知（钉钉 / 企微 / 邮件）
