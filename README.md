<p align="center">
 <img src="https://img.shields.io/badge/bubble-1.0.0-success.svg" alt="Build Status">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2025-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.5-blue.svg" alt="Downloads">
</p>

## 系统说明

- 基于 Spring Cloud 、Spring Boot、 OAuth2 的 RBAC **企业快速开发平台**， 同时支持微服务架构和单体架构
- 提供对 Spring Authorization Server 生产级实践，支持多种安全授权模式
- 提供对常见容器化方案支持 Kubernetes、Rancher2 、Kubesphere、EDAS、SAE 支持

## 快速开始

分支说明
- JDK17_master: java17 + springboot 3.5 + springcloud 2025
- JDK8_master: java8 + springboot 2.7 + springcloud 2021


### 核心依赖

| 依赖                          | 版本     |
|-----------------------------|--------|
| Spring Boot                 | 3.5.9  |
| Spring Cloud                | 2025   |
| Spring Cloud Alibaba        | 2025   |
| Spring Authorization Server | 1.5.2  |
| Mybatis Plus                | 3.5.15 |
| Vue                         | 3.5    |
| Element Plus                | 2.8    |

### 模块说明

```lua
bubble-back  -- https://github.com/qinlei131479/bubble-back.git

bubble
└── bubble-api -- 通用API模块
     ├── bubble-api-backend -- 通用用户权限管理系统公共api模块
     ├── bubble-api-flow -- 通用flow公共api模块
     └── bubble-api-oa -- 通用oa公共api模块
├── bubble-auth -- 授权服务提供[8766]
└── bubble-biz -- 通用用户权限管理模块
     ├── bubble-biz-backend -- 通用用户权限管理系统业务处理模块[8801]
     ├── bubble-biz-flow -- 通用flow业务处理模块[8802]
     └── bubble-biz-oa -- 通用OA业务处理模块[8803]
└── bubble-common -- 系统公共模块
     ├── bubble-common-bom -- 全局依赖管理控制
     ├── bubble-common-core -- 公共工具类核心包
     ├── bubble-common-datasource -- 动态数据源包
     ├── bubble-common-log -- 日志服务
     ├── bubble-common-oss -- 文件上传工具类
     ├── bubble-common-mybatis -- mybatis 扩展封装
     ├── bubble-common-seata -- 分布式事务
     ├── bubble-common-websocket -- websocket 封装
     ├── bubble-common-security -- 安全工具类
     ├── bubble-common-swagger -- 接口文档
     ├── bubble-common-feign -- feign 扩展封装
     └── bubble-common-xss -- xss 安全封装
├── bubble-gateway -- Spring Cloud Gateway网关[8666]
└── bubble-visual
     └── bubble-codegen -- 图形化代码生成 [8901]
     ├── bubble-monitor -- 服务监控 [8902]
     └── bubble-quartz -- 定时任务管理台 [8903]
```