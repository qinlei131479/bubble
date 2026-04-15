# API 约定

## URL 规范

- 网关统一入口：`http://<host>:8666`
- 后端路由前缀由网关根据服务名分发
- OA 模块路由与 PHP 原始路径对齐

## HTTP 方法

| 操作 | 方法 | 路径示例 |
|------|------|----------|
| 分页列表 | GET | `/page` 或 `""` |
| 详情 | GET | `/{id}/edit` 或 `/{id}` |
| 新增 | POST | `""` |
| 修改 | PUT | `/{id}` |
| 删除 | DELETE | `/{id}` |

## 分页参数

使用 `Pg` 对象接收：

| 参数 | 说明 |
|------|------|
| `current` | 当前页码 |
| `size` | 每页条数 |

## 认证

- 请求头：`Authorization: Bearer <token>`
- 内部服务调用：`@Inner` 注解免鉴权
- 权限校验：`@HasPermission("xxx")` 注解

## 请求/响应加密

- Header：`Enc-Flag` 标识
- 密钥：`VITE_PWD_ENC_KEY` 环境变量
