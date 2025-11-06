<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2021-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-2.7-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.2-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/github/license/pig-mesh/pig"/>
</p>

## 系统说明

- 基于 Spring Cloud 2021 、Spring Boot 2.7、 OAuth2 的 RBAC **权限管理系统**
- 基于数据驱动视图的理念封装 element-plus，即使没有 vue 的使用经验也能快速上手
- 提供对常见容器化支持 Docker、Kubernetes、Rancher2 支持
- 提供 lambda 、stream api 、webflux 的生产实践

## 快速开始

### 分支说明

- JDK8_master: java8 + springboot 2.7 + springcloud 2021
- JDK17_master: java17 + springboot 3.5 + springcloud 2025

### 核心依赖

| 依赖                          | 版本         |
|-----------------------------|------------|
| Spring Boot                 | 2.7.18     |
| Spring Cloud                | 2021.0.8   |
| Spring Cloud Alibaba        | 2021.0.6.1 |
| Spring Authorization Server | 0.4.4      |
| Mybatis Plus                | 3.5.7      |
| hutool                      | 5.8.29     |

### 模块说明

```lua
bubble-back  -- https://github.com/qinlei131479/bubble-back.git

pig
└── bubble-api -- 通用用户权限管理模块
     └── bubble-api-backend- -- 通用用户权限管理系统公共api模块
├── bubble-auth -- 授权服务提供[8766]
└── bubble-biz -- 通用用户权限管理模块
     └── bubble-biz-backend -- 通用用户权限管理系统业务处理模块[8801]
└── bubble-common -- 系统公共模块
     ├── bubble-common-bom -- 全局依赖管理控制
     ├── bubble-common-core -- 公共工具类核心包
     ├── bubble-common-datasource -- 动态数据源包
     ├── bubble-common-job -- xxl-job 封装
     ├── bubble-common-log -- 日志服务
     ├── bubble-common-oss -- 文件上传工具类
     ├── bubble-common-mybatis -- mybatis 扩展封装
     ├── bubble-common-seata -- 分布式事务
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

### 本地开发 运行

bubble 提供了详细的，包括开发环境安装、服务端代码运行、前端代码运行等。

请务必**完全按照**文档部署运行章节 进行操作，减少踩坑弯路！！


### Docker 运行

```
# 下载并运行服务端代码
git clone https://github.com/qinlei131479/bubble.git

cd bubble && mvn clean install && docker-compose up -d

# 下载并运行前端UI
git clone https://github.com/qinlei131479/bubble-back.git

cd bubble-back && npm install -g cnpm --registry=https://registry.npm.taobao.org


cnpm install && cnpm run build:docker && cd docker && docker-compose up -d
```


