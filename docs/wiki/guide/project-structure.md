# 目录结构

## Monorepo 总览

```text
Bubble/
├── apps/
│   ├── bubble-cloud/            # Java 微服务后端（聚合工程）
│   │   ├── bubble-gateway/      # Spring Cloud Gateway 网关
│   │   ├── bubble-auth/         # OAuth2 认证授权中心
│   │   ├── bubble-api/          # 公共 API（实体/DTO/Feign，jar 库）
│   │   ├── bubble-biz/          # 业务服务（可独立部署）
│   │   ├── bubble-common/       # 跨模块公共组件
│   │   ├── bubble-visual/       # 可视化管理服务
│   │   └── pom.xml              # Maven 聚合根
│   ├── bubble-ui/               # Vue 3 前端应用
│   └── agentic/                 # Python 智能体服务（规划中）
├── docker/                      # 容器编排与部署配置
├── docs/                        # VitePress 知识库（本文档）
├── script/                      # 脚本（数据库初始化、部署）
│   ├── db/                      # SQL 初始化 + MySQL Dockerfile
│   └── deploy/                  # 部署脚本
└── .cursor/rules/               # AI 开发规范（VibeCoding）
```

## 设计原则

- **apps/**：所有可运行的应用代码，按技术栈/服务分目录
- **docker/**：只放 Dockerfile、compose、配置，不放业务代码
- **docs/**：可发布的知识库，单一事实源
- **script/**：一次性或低频使用的脚本
- **.cursor/rules/**：面向 AI 协作的开发约束
