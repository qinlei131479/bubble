# 错误码

## 统一返回结构

```json
{
  "code": 0,
  "msg": "ok",
  "data": {}
}
```

- `code = 0`：成功
- `code = 1`：业务失败（`msg` 中携带错误信息）

## OA 兼容格式

OA 模块额外包含 `status` 字段：

```json
{
  "code": 0,
  "status": 200,
  "msg": "ok",
  "data": {}
}
```

- `status = 200`：成功
- `status = 400`：失败

## HTTP 状态码约定

| 状态码 | 含义 |
|--------|------|
| 200 | 正常 |
| 401 | 未认证 |
| 403 | 无权限 |
| 424 | Token 过期，前端触发重新登录 |
| 500 | 服务器内部错误 |

## 国际化

错误码与 `i18n/messages` 资源文件配套，通过 `R.failed(ErrorCodes.XXX)` 使用。
