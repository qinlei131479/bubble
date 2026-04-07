# 阶段二：配置中心与系统管理 (P0)

> **优先级**: P0 — 权限、字典、附件被所有业务模块依赖  
> **前置依赖**: 阶段一（登录/用户/组织架构）  
> **预估工时**: 1.5-2 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

> **文档进度**（2026-04）：核心权限链、云存储 elForm、快捷入口与分类 elForm、附件分类创建 elForm、字典 `info`/`tree`、企业配置防火墙等接口已与代码对齐；附件在线编辑仍为明确不支持；消息远程 `sync`、升级远程能力仍为占位。

---

## 一、目标与验收标准

前端系统管理模块完整可用：**菜单 CRUD → 角色 CRUD 含权限绑定 → 字典管理 → 表单配置 → 快捷入口管理 → 云存储配置 → 附件管理 → 消息系统 → 协议管理 → 日志查看**

### 验收检查清单

- [x] 菜单 CRUD 含权限节点保存（`not_save` / `save_enterprise` 已实现）
- [x] 角色管理：创建/编辑/权限分配/成员管理/密码修改（含超级角色 `super_role` / `super_role_tree`）
- [x] 字典类型与字典数据 CRUD，tree 结构正确（`GET .../info/{id}`、`POST .../tree` 已实现）
- [x] 表单分组与表单数据管理（接口清单 3.9 已标 ✅）
- [x] 快捷入口与分类管理（elForm + `show` 状态）
- [x] 云存储列表/配置/状态切换（elForm；同步为密钥校验占位）
- [x] 附件管理（上传/分类/移动/删除）；在线编辑内容仍占位
- [x] 系统消息列表/分类/订阅（`PUT subscribe/{id}`；`PUT sync` 远程同步占位）
- [x] 协议管理读写
- [x] 系统日志查看
- [x] 客户端规则配置（审批规则含 OaElFormVO）
- [ ] 本阶段所有占位桩已替换为真实逻辑（附件在线编辑、消息远程同步、升级远程接口仍为占位或明确降级）

---

## 二、模块执行顺序

```
1. DictTypeController / DictDataController      — 字典被多处引用
2. SystemMenusController                         — 菜单是权限基础
3. EnterpriseRoleController                      — 角色绑定菜单权限
4. SystemAttachController / AttachCateController  — 附件存储被全系统依赖
5. SystemStorageController                        — 云存储配置
6. MessageController / EnterpriseMessageNotice    — 消息系统
7. FormController / FormDataController            — 表单配置
8. QuickController / QuickCateController          — 快捷入口
9. ClientRuleController                           — 客户端配置规则
10. ConfigAdminController                         — 企业配置
11. AgreementAdminController                      — 协议管理
12. EnterpriseLogController                       — 系统日志
13. UpgradeAdminController                        — 在线升级
```

---

## 三、接口清单

### 3.1 DictTypeController — 字典类型

**PHP Prefix**: `ent/config/dict_type` | **Java**: `@RequestMapping("/ent/config/dict_type")`  
**状态**: 已有，需核验完整性

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 字典类型列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存字典类型 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改字典类型 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除字典类型 | ✅ |
| 7 | GET | /info/{id} | info | 字典类型详情（含可删标记） | ✅ |

### 3.2 DictDataController — 字典数据

**PHP Prefix**: `ent/config/dict_data` | **Java**: `@RequestMapping("/ent/config/dict_data")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 字典数据列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存字典数据 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改字典数据 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除字典数据 | ✅ |
| 7 | POST | /tree | tree | 树形结构 | ✅ |

### 3.3 SystemMenusController — 菜单管理

**PHP Prefix**: `ent/system/menus` | **Java**: `@RequestMapping("/ent/system/menus")`  
**状态**: ✅ 已补全 not_save / save_enterprise（对接 `eb_system_role` 与规则）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 菜单列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存菜单 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改菜单 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除菜单 | ✅ |
| 7 | GET | /{id} | show | 菜单详情 | ✅ |
| 8 | POST | /not_save | notSave | 获取未保存权限 | ✅ |
| 9 | POST | /save_enterprise | saveEnterprise | 保存企业菜单到超级角色 | ✅ |

**实现要点**:
- `not_save`: 查询角色未绑定的菜单节点列表，返回 tree 结构
- `save_enterprise`: 将选中菜单 ID 批量关联到指定超级角色

### 3.4 EnterpriseRoleController — 角色管理

**PHP Prefix**: `ent/system/roles` | **Java**: `@RequestMapping("/ent/system/roles")`  
**状态**: ✅ 超级角色接口已落地（`eb_system_role` + 菜单 rules/apis）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 角色列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存角色 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改角色 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除角色 | ✅ |
| 7 | GET | /user/{id} | getUsers | 角色用户列表 | ✅ |
| 8 | GET | /role/{uid} | getUserRoles | 用户角色列表 | ✅ |
| 9 | POST | /user | setUserRole | 修改用户角色 | ✅ |
| 10 | POST | /add_user | addUser | 角色新增用户 | ✅ |
| 11 | POST | /show_user | showUser | 修改角色成员状态 | ✅ |
| 12 | DELETE | /del_user | delUser | 删除角色成员 | ✅ |
| 13 | POST | /pwd | resetPwd | 修改用户密码 | ✅ |
| 14 | POST | /update_super_role | updateSuperRole | 修改超级角色权限（PHP 为 PUT `/super_role`） | ✅ |
| 15 | GET | /get_super_role | getSuperRole | 超级角色权限树（PHP 为 `super_role_tree`） | ✅ |

### 3.5 SystemAttachController — 附件管理

**PHP Prefix**: `ent/system/attach` | **Java**: `@RequestMapping("/ent/system/attach")`  
**状态**: 核心能力已齐；在线编辑已明确降级

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 附件列表 | ✅ |
| 2 | POST | / | store | 上传附件 | ✅ |
| 3 | DELETE | /{id} | destroy | 删除附件 | ✅ |
| 4 | PUT | /move/{id} | move | 移动分类 | ✅ |
| 5 | PUT | /rename/{id} | rename | 重命名 | ✅ |
| 6 | POST | /upload | upload | 文件上传 | ✅ |
| 7 | GET | /cover/{id} | cover | 获取封面 | ✅ |
| 8 | POST | /file_upload | fileUpload | 大文件上传 | ✅ |
| 9 | GET | /chunk_list | chunkList | 分片列表 | ✅ |
| 10 | GET | /merge | merge | 合并分片 | ✅ |
| 11 | DELETE | /batch | batchDestroy | 批量删除 | ✅ |
| 12 | PUT | /info/{id}（multipart） | updateInfo | 内容更新：PHP 含 Word 转换；Java 返回明确「不支持在线编辑」 | ⚠️ 已降级（非未实现） |
| 13 | GET | /download/{id} | download | 下载 | ✅ |

### 3.6 AttachCateAdminController — 附件分类

**PHP Prefix**: `ent/system/attach_cate` | **Java**: `@RequestMapping("/ent/system/attach_cate")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 分类列表 | ✅ |
| 2 | GET | /create | create | 添加表单（OaElFormVO） | ✅ |
| 3 | POST | / | store | 保存分类 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改分类 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除分类 | ✅ |

### 3.7 SystemStorageController — 云存储管理

**PHP Prefix**: `ent/config/storage` | **Java**: `@RequestMapping("/ent/config/storage")`  
**状态**: elForm 已对齐；`sync` 为密钥校验（未接云 SDK）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /index | list | 云存储列表 | ✅ |
| 2 | GET | /create/{type} | createForm | 添加云空间 elForm | ✅ |
| 3 | POST | /{type} | create | 保存存储行 + 密钥写 config | ✅ |
| 4 | GET | /config | getConfig | 当前上传方式 | ✅ |
| 5 | POST | /config | saveConfig | 保存密钥配置 | ✅ |
| 6 | GET | /form/{type} | form | 密钥配置 elForm（对齐 PHP getFormStorageConfig） | ✅ |
| 7 | PUT | /save_basic | updateConfig | 水印/缩略图等（非单桶编辑） | ✅ |
| 8 | GET | /sync/{type} | sync | 同步：校验密钥已配置 | ⚠️ 未调用 S3（可后续增强） |
| 9 | PUT | /status/{id} | updateStatus | 启用存储 | ✅ |
| 10 | GET | /domain/{id} | getUpdateDomainForm | 域名 elForm | ✅ |
| 11 | POST | /domain/{id} | updateDomain | 修改域名 | ✅ |
| 12 | DELETE | /{id} | removeById | 删除 | ✅ |
| 13 | PUT | /save_type/{type} | uploadType | 切换上传方式 | ✅ |
| 14 | GET | /method | getStorageConfig | 详细配置项 | ✅ |

**实现要点**:
- `create/{type}` / `form/{type}`：返回 `OaElFormVO`；`sync/{type}` 当前仅校验 `eb_system_config` 中对应密钥键是否存在。

### 3.8 MessageController — 系统消息

**PHP Prefix**: `ent/system/message` | **Java**: `@RequestMapping("/ent/system/message")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /list | list | 消息列表（list+count） | ✅ |
| 2 | GET | /cate | cate | 分类 + 未读数 | ✅ |
| 3 | GET | /find/{id} | find | 消息详情 | ✅ |
| 4 | PUT | /update/{id} | update | 修改消息（提醒时间/渠道模板） | ✅ |
| 5 | PUT | /status/{id}/{type} | status | 按渠道类型改模板状态 | ✅ |
| 6 | PUT | /subscribe/{id} | subscribe | 用户是否可取消订阅（PHP 无 GET subscribe） | ✅ |
| 7 | PUT | /sync | sync | 同步模板（远程未对接，空操作返回 ok） | ⚠️ 占位 |

### 3.9 FormDataController — 表单配置

**PHP Prefix**: `ent/config/form` | **Java**: `@RequestMapping("/ent/config/form")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /cate | index | 表单分组列表 | ✅ |
| 2 | POST | /cate | store | 保存分组 | ✅ |
| 3 | GET | /cate/{id} | show | 分组状态 | ✅ |
| 4 | PUT | /cate/{id} | update | 修改分组 | ✅ |
| 5 | DELETE | /cate/{id} | destroy | 删除分组 | ✅ |
| 6 | POST | /data/{types} | storeData | 保存表单 | ✅ |
| 7 | PUT | /data/move/{types} | move | 移动分组 | ✅ |
| 8 | GET | /data/fields/{customType} | getFields | 业务数据字段 | ✅ |
| 9 | PUT | /data/fields/{customType} | saveFields | 保存业务字段 | ✅ |

### 3.10 SystemQuickController — 快捷入口

**PHP Prefix**: `ent/config/quick` | **Java**: `@RequestMapping("/ent/config/quick")`  
**状态**: ✅ elForm + show

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create | createForm | 创建 elForm | ✅ |
| 3 | POST | / | create | 保存 | ✅ |
| 4 | GET | /{id}/edit | editForm | 编辑 elForm | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | removeById | 删除 | ✅ |
| 7 | GET | /{id}?status= | show | 显示/隐藏（改 `status`，对齐 PHP Resource show） | ✅ |

### 3.11 CategoryController — 快捷分类

**PHP Prefix**: `ent/config/quickCate` | **状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create | createForm | 创建 elForm | ✅ |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |

### 3.12 ClientRuleController — 客户端配置规则

**PHP Prefix**: `ent/config/client_rule` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 分类列表 | ✅ |
| 2 | GET | /{category} | getConfig | 获取配置 | ✅ |
| 3 | PUT | /{category} | saveConfig | 保存配置 | ✅ |
| 4 | GET | /approve | approveList | 审批规则列表 | ✅ |
| 5 | GET | /approve/{form} | approveForm | 审批规则表单 | ✅ |
| 6 | PUT | /approve | updateApprove | 更新审批开关 | ✅ |

### 3.13 EnterpriseConfigController — 企业配置

**PHP Prefix**: `ent/config` | **状态**: ✅ 已实现（Java 路径 `PUT /data/all/{category}` 按分类标识保存）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /work_bench | getWorkBenchFrom | 工作台配置 | ✅ |
| 2 | POST | /work_bench | saveWorkBenchFrom | 保存工作台配置 | ✅ |
| 3 | GET | /data/updateConfig | updateConfig | 按 category 拉取配置行 | ✅ |
| 4 | PUT | /data/all/{category} | updateAll | 按分类保存配置 | ✅ |
| 5 | GET | /data/firewall | firewallConfig | 防火墙配置 | ✅ |
| 6 | PUT | /data/firewall | saveFirewallConfig | 保存防火墙 | ✅ |
| 7 | GET | /cate | configCate | 配置分类列表 | ✅ |

### 3.14 AgreementAdminController — 协议管理

**PHP Prefix**: `ent/system/treaty` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /{id} | show | 详情 | ✅ |
| 3 | PUT | /{id} | update | 修改 | ✅ |

### 3.15 EnterpriseLogController — 系统日志

**PHP Prefix**: `ent/system/log` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 日志列表 | ✅ |

### 3.16 UpgradeAdminController — 在线升级

**PHP Prefix**: `ent/system/upgrade` | **状态**: 占位（无远程升级服务）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /status | status | 升级状态 | ✅ |
| 2 | GET | /agreement | agreement | 升级协议 | ⚠️ 占位，返回空协议内容即可 |
| 3 | GET | / | index | 升级包列表 | ✅ |
| 4 | GET | /key | key | 获取 Key | ✅ |
| 5 | POST | /start | start | 开始升级 | ⚠️ 占位，返回"不支持在线升级" |
| 6 | GET | /progress | progress | 升级进度 | ⚠️ 占位 |
| 7 | GET | /log | log | 升级日志 | ⚠️ 占位 |

> 升级功能 PHP 侧也是远程服务调用，Java 侧保留路由返回明确提示即可，不算占位桩。

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_dict_type | DictType | ✅ 已有 |
| eb_dict_data | DictData | ✅ 已有 |
| eb_system_menus | SystemMenus | ✅ 已有 |
| eb_enterprise_role | EnterpriseRole | ✅ 已有 |
| eb_system_role | SystemRole | ✅ 已有（企业超级角色模板） |
| eb_rules | Rules | ✅ 已对接（角色/超级角色） |
| eb_system_attach | SystemAttach | ✅ 已有 |
| eb_system_attach_cate | SystemAttachCategory | ✅ 已有 |
| eb_system_storage | SystemStorage | ✅ 已有 |
| eb_message_category | MessageCategory | ✅ 已有 |
| eb_form_cate | FormCategory | ✅ 已有 |
| eb_form_data | FormData | ✅ 已有 |
| eb_system_quick | SystemQuick | ✅ 已有（eb_system_quick） |
| eb_quick_cate | QuickCategory | ⚠️ PHP 表名多为 eb_category.type=quickConfig |
| eb_agreement | Agreement | ✅ 已有 |
| eb_system_config | SystemConfig | ✅ 已有 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 | 状态 |
|------|------|--------|--------|------|
| 1 | 实现未保存权限接口 | SystemMenusController | P0 | ✅ |
| 2 | 实现保存企业菜单到超级角色 | SystemMenusController | P0 | ✅ |
| 3 | 实现超级角色权限修改与权限树 | EnterpriseRoleController | P0 | ✅ |
| 4 | 消除云存储创建/配置/同步/域名表单占位 | SystemStorageController | P1 | ✅（sync 为密钥校验） |
| 5 | 附件在线编辑/Word 转换 | SystemAttachController | P2 | ⚠️ 明确不支持（可后续接 OnlyOffice 等） |
| 6 | 核验企业配置防火墙等接口 | EnterpriseConfigController | P1 | ✅ |
| 7 | 快捷入口创建表单与显示切换 | SystemQuickController | P1 | ✅ |
| 8 | 快捷分类创建表单 | CategoryController | P2 | ✅ |
| 9 | 消息订阅与同步 | MessageController | P1 | ✅ subscribe；⚠️ 远程 sync 占位 |
| 10 | 附件分类创建表单 | AttachCateAdminController | P2 | ✅ |
| 11 | 云存储真实 listBuckets 校验（可选） | SystemStorageController | P3 | 待办 |
