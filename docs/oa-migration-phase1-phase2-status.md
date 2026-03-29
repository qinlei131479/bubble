# OA 迁移 Phase 1 / Phase 2 — 功能整理与未完项

> **关联计划**：`~/.cursor/plans/php-to-java_oa_migration_6dd94852.plan.md`  
> **说明**：计划文件中 Phase 1、Phase 2 标记为「已完成」指阶段目标已启动并有代码落地；本文按**实际代码与 PHP 对标范围**区分「已对齐」「部分完成」「未做/占位」，便于后续收尾。

---

## Phase 1：基础设施与认证桥接（计划约 15 接口）

### 目标（计划原文）

OA 可启动、连接 PHP 库、PHP 前端可登录获取数据；数据源、`eb_` 前缀、PHP JWT、网关路由、`Login`/`Common`、核心 Entity 等。

### 已实现（可视为基线）

| 项 | 说明 |
|----|------|
| PHP JWT 验签 | `OaPhpJwtAuthenticationFilter`、`OaPhpJwtTokenService`、`OaPhpJwtSecurityConfig`；安全匹配 **`/ent/**` 与 `/oa/**`**（与计划文档中仅写 `/oa/**` 略有差异，以代码为准） |
| 登录主流程 | `POST /ent/user/login`：账号密码 + BCrypt（含 PHP `$2y$`→`$2a$` 兼容）、签发 JWT |
| 用户信息 | `GET /ent/user/info`：`AuthServiceImpl.loginInfo` 组装用户与企业等 |
| 退出 | `GET /ent/user/logout`：返回成功（未强制失效服务端会话/Token 黑名单，若 PHP 有则需对齐） |
| 修改密码 | `PUT /ent/user/savePassword`（及 `common/savePassword` 别名） |
| 公共配置 | `GET /ent/common/config`：`SystemConfigService` 按 `type` 读 `eb_system_config` |
| 站点信息 | `GET /ent/common/site`：`SiteServiceImpl` 聚合企业与系统配置 |
| 核心实体（部分） | `Admin`、`Enterprise`、`SystemMenus`、`EnterpriseRole`、`SystemConfig` 等已在 `bubble-api-oa` 使用 |

### 未完 / 占位 / 需验收

| 项 | 缺口说明 |
|----|----------|
| **登录扩展** | `POST /ent/user/register`、`/phone_login`、`GET/POST scan_key` / `scan_status` 明确返回「尚未实现」 |
| **验证码** | `GET /ent/common/captcha` 为占位（固定 key、空图/空串类实现），未对接真实图形验证码与校验流程 |
| **公共消息/授权** | `GET /ent/common/message`、`/auth` 为占位结构（空列表或固定数值），未对接 `eb_message` 等与 PHP 一致的业务数据 |
| **计划中的 Entity** | **`eb_admin_info`（AdminInfo）** 在 `bubble-api-oa` 中**未见实体类**；若登录/档案依赖需补表模型 |
| **`eb_user_token`** | 未见与 PHP 一致的 Token 落库/吊销策略；若前端依赖服务端踢线或多端，需补 |
| **基础设施** | **Nacos 数据源、`bubble-biz-oa-dev.yml`、网关 `Path=/oa/**` → StripPrefix** 等属**部署与环境**，代码库内无法 100% 验证，需单独 checklist 验收 |
| **依赖** | `pom.xml` 中 jjwt 等与计划一致与否，以仓库为准；若缺版本可随收尾统一核对 |

---

## Phase 2：组织架构与系统配置（计划约 70 接口）

### 目标（计划原文）

组织架构树 + 菜单角色 + 字典配置「全部可用」；涉及 Frame、菜单、角色、Casbin、字典、系统/企业配置、表单、快捷入口、协议等。

### 已实现较完整

| 模块 | Java | 说明 |
|------|------|------|
| 组织架构 | `FrameController`（`/ent/config/frame`） | 部门树、权限树、人员树、表单、增删改查、成员、管理范围等，与 PHP 该段能力**基本对齐**（以当前 `FrameServiceImpl` 为准） |
| 字典类型（只读分页） | `DictTypeController`（`/ent/config/dict_type`） | `GET` 分页列表，读 `eb_dict_type` |

### 部分完成（仅部分接口或只读）

| 模块 | 现状 | 相对 PHP 缺口 |
|------|------|----------------|
| 企业菜单 | `MenusAdminController` | **仅有**菜单树列表 `GET /ent/system/menus`；缺 PHP `MenuController` 常见的增删改、排序、按钮级等约 **~11 接口**量级 |
| 企业角色 | `RolesAdminController` | **仅有**角色列表 `GET /ent/system/roles`；缺角色 CRUD、规则 `rules/apis`、用户绑定、数据权限等约 **~18 接口**量级 |
| 字典数据 | 无独立 `DictDataController` | `DictTypeController` 未覆盖字典项 CRUD；`ConfigController` 下 `/dict/type/page`、`/dict/data/page` 为 **SimplePageVO.empty 占位** |
| 系统配置扩展 | `ConfigController` | `/client_rule/approve/{isForm}` 为静态默认 VO；`/dict/*`、`/form/page` 为**空分页占位** |
| 系统管理占位 | `SystemController` | `/menu/page`、`/role/page`、`/log/page` 均为**空分页占位**，与真实菜单/角色/日志表无关 |

### 计划表中明确、但代码侧基本未落地的表/能力

| 计划项 | 说明 |
|--------|------|
| **`eb_rules`（Casbin）** | 未见 Java 侧规则同步、校验与 PHP `eb_rules` 对齐的实现 |
| **`eb_enterprise_config`** | 未见 `EnterpriseConfig` 实体及独立维护接口（企业维度配置可能在 PHP 中与 `system_config` 混用，需对标接口再定） |
| **`eb_form_cate` / `eb_form_data`** | 仅 `ConfigController` 表单分页占位，无真实 CRUD |
| **`eb_system_quick` / `eb_user_quick`** | 未见对应 `QuickController` / 快捷入口业务 |
| **`eb_agreement`** | 未见协议相关 Controller/Entity |
| **系统配置 8 接口量级** | PHP `SysremConfigController` 等若包含多类 `type`/分组，Java 需逐项对照；当前以 `SystemConfigService.config(ConfigQueryDTO)` 为主，**未必覆盖全部 PHP 路径** |

---

## 汇总：Phase 1 / Phase 2「未完」记录（收尾阶段处理）

1. **认证与公共**：注册、短信登录、扫码登录链路；真实验证码；`common/message`、`common/auth` 业务数据；Token 与踢线策略（若需要）。  
2. **数据模型**：`AdminInfo`、`UserToken`（若产品需要）；企业配置表是否单独建模。  
3. **菜单 / 角色**：从「列表/树」补全到与 PHP 接近的 **CRUD + 权限绑定**。  
4. **字典**：字典类型除分页外的维护；**字典数据**全套接口。  
5. **配置与表单**：`ConfigController` 占位接口换真实实现；**表单分类/表单数据**；**快捷入口**；**协议**。  
6. **权限规则**：**Casbin / `eb_rules`** 与菜单、接口权限一致。  
7. **系统占位**：`SystemController` 中菜单/角色分页、**操作日志**等与真实表与 PHP 路由对齐。  
8. **环境与网关**：Nacos 数据源、网关路由、生产 JWT 密钥与 PHP 一致；路径前缀 **`/oa/**` vs 实际业务 `/ent/**`** 在网关上的转发规则需与运维文档一致。

---

## 变更记录

| 日期 | 说明 |
|------|------|
| 2026-03-29 | 初版：根据仓库代码与迁移计划整理 Phase 1 / Phase 2 已完成与未完项 |
