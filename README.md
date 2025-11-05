<p align="center">
 <img src="https://img.shields.io/badge/Pig-1.0.0-success.svg" alt="Build Status">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2025-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.5-blue.svg" alt="Downloads">
</p>

## 系统说明

- 基于 Spring Cloud 、Spring Boot、 OAuth2 的 RBAC **企业快速开发平台**， 同时支持微服务架构和单体架构
- 提供对 Spring Authorization Server 生产级实践，支持多种安全授权模式
- 提供对常见容器化方案支持 Kubernetes、Rancher2 、Kubesphere、EDAS、SAE 支持

## 快速开始

#### Docker 快速体验

```shell
# 可用内存大于4G
curl -o docker-compose.yaml https://try.pig4cloud.com
# 等待5分钟
docker compose up
```

### 核心依赖

| 依赖                         | 版本     |
|-----------------------------|--------|
| Spring Boot                 | 3.5.7  |
| Spring Cloud                | 2025   |
| Spring Cloud Alibaba        | 2025   |
| Spring Authorization Server | 1.5.2  |
| Mybatis Plus                | 3.5.14 |
| Vue                         | 3.5    |
| Element Plus                | 2.7    |

### 模块说明

```lua
bubble-back  -- https://github.com/qinlei131479/bubble-back.git

pig
├── pig-auth -- 授权服务提供[3000]
└── pig-common -- 系统公共模块
     ├── pig-common-bom -- 全局依赖管理控制
     ├── pig-common-core -- 公共工具类核心包
     ├── pig-common-datasource -- 动态数据源包
     ├── pig-common-log -- 日志服务
     ├── pig-common-oss -- 文件上传工具类
     ├── pig-common-mybatis -- mybatis 扩展封装
     ├── pig-common-seata -- 分布式事务
     ├── pig-common-websocket -- websocket 封装
     ├── pig-common-security -- 安全工具类
     ├── pig-common-swagger -- 接口文档
     ├── pig-common-feign -- feign 扩展封装
     └── pig-common-xss -- xss 安全封装
├── pig-register -- Nacos Server[8848]
├── pig-gateway -- Spring Cloud Gateway网关[9999]
└── pig-upms -- 通用用户权限管理模块
     └── pig-upms-api -- 通用用户权限管理系统公共api模块
     └── pig-upms-biz -- 通用用户权限管理系统业务处理模块[4000]
└── pig-visual
     └── pig-monitor -- 服务监控 [5001]
     ├── pig-codegen -- 图形化代码生成 [5002]
     └── pig-quartz -- 定时任务管理台 [5007]
```