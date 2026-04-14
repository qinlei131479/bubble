# 数据库设计

## 数据库实例

| 库名 | 用途 | 初始化脚本 |
|------|------|-----------|
| bubble | 业务主库 | `script/db/bubble.sql` |
| bubble_config | Nacos 配置中心持久化 | `script/db/bubble_config.sql` |

## 业务库 (pig) 核心表

### 系统管理

| 表名 | 用途 |
|------|------|
| sys_user | 用户信息 |
| sys_role | 角色定义 |
| sys_menu | 菜单与按钮权限 |
| sys_dept | 部门树 |
| sys_post | 岗位 |
| sys_user_role | 用户-角色关联 |
| sys_role_menu | 角色-菜单关联 |
| sys_dict / sys_dict_item | 数据字典 |
| sys_public_param | 系统公共参数 |
| sys_log | 操作日志 |
| sys_file | 文件上传元数据 |
| sys_oauth_client_details | OAuth2 客户端配置 |

### 定时任务

| 表名 | 用途 |
|------|------|
| sys_job | 业务任务定义 |
| sys_job_log | 任务执行日志 |
| QRTZ_* | Quartz JDBC JobStore 标准表 |

### 代码生成

| 表名 | 用途 |
|------|------|
| gen_datasource_conf | 代码生成数据源 |
| gen_table / gen_table_column | 待生成表及列元数据 |
| gen_template / gen_group | 模板与分组 |

### OA 业务（`eb_*` 前缀）

OA 模块使用 `eb_` 前缀的表，共约 219 张，实体通过 `@TableName("eb_xxx")` 映射。

## 表设计约定

- **审计字段**: `create_by`, `create_time`, `update_by`, `update_time`（MybatisPlusMetaObjectHandler 自动填充）
- **逻辑删除**: `del_flag`（0=正常, 1=删除）
- **租户隔离**: `tenant_id`（多租户场景）
- **主键**: 自增 BIGINT
- **字符集**: utf8mb4
