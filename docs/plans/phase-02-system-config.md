# 阶段二：配置中心与系统管理 (P0)

> **优先级**: P0 — 权限、字典、附件被所有业务模块依赖  
> **前置依赖**: 阶段一（登录/用户/组织架构）  
> **预估工时**: 1.5-2 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端系统管理模块完整可用：**菜单 CRUD → 角色 CRUD 含权限绑定 → 字典管理 → 表单配置 → 快捷入口管理 → 云存储配置 → 附件管理 → 消息系统 → 协议管理 → 日志查看**

### 验收检查清单

- [ ] 菜单 CRUD 含权限节点保存
- [ ] 角色管理：创建/编辑/权限分配/成员管理/密码修改
- [ ] 字典类型与字典数据 CRUD，tree 结构正确
- [ ] 表单分组与表单数据管理
- [ ] 快捷入口与分类管理
- [ ] 云存储列表/配置/状态切换
- [ ] 附件管理（上传/分类/移动/删除）
- [ ] 系统消息列表/分类/订阅
- [ ] 协议管理读写
- [ ] 系统日志查看
- [ ] 客户端规则配置（审批规则含 OaElFormVO）
- [ ] 本阶段所有占位桩已替换为真实逻辑

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
| 7 | GET | /info/{id} | info | 字典类型详情 | ⚠️ 需核验 |

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
| 7 | POST | /tree | tree | 树形结构 | ⚠️ 需核验 |

### 3.3 SystemMenusController — 菜单管理

**PHP Prefix**: `ent/system/menus` | **Java**: `@RequestMapping("/ent/system/menus")`  
**状态**: 已有，需补全 2 个占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 菜单列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存菜单 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改菜单 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除菜单 | ✅ |
| 7 | GET | /{id} | show | 菜单详情 | ✅ |
| 8 | POST | /not_save | notSave | 获取未保存权限 | ⚠️ 占位，需实现：比对 `eb_system_menus` 与角色已分配权限差集 |
| 9 | POST | /save_enterprise | saveEnterprise | 保存企业菜单到超级角色 | ⚠️ 占位，需实现：批量写入 `eb_rules` 关联 |

**实现要点**:
- `not_save`: 查询角色未绑定的菜单节点列表，返回 tree 结构
- `save_enterprise`: 将选中菜单 ID 批量关联到指定超级角色

### 3.4 EnterpriseRoleController — 角色管理

**PHP Prefix**: `ent/system/roles` | **Java**: `@RequestMapping("/ent/system/roles")`  
**状态**: 已有，需补全 2 个占位

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
| 14 | PUT | /super_role | updateSuperRole | 修改超级角色权限 | ⚠️ 占位，需实现：更新 `eb_rules` 权限关联 |
| 15 | GET | /super_role_tree | superRoleTree | 超级角色权限树 | ⚠️ 占位，需实现：查 `eb_system_menus` + `eb_rules` 构建权限树 |

### 3.5 SystemAttachController — 附件管理

**PHP Prefix**: `ent/system/attach` | **Java**: `@RequestMapping("/ent/system/attach")`  
**状态**: 已有，需补全 1 处占位

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
| 12 | POST | /content/{id} | updateContent | 内容更新（PHP 含 Word 转换） | ⚠️ 占位，需实现或标记不支持 |
| 13 | GET | /download/{id} | download | 下载 | ✅ |

### 3.6 AttachCateAdminController — 附件分类

**PHP Prefix**: `ent/system/attach_cate` | **Java**: `@RequestMapping("/ent/system/attach_cate")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 分类列表 | ✅ |
| 2 | GET | /create | create | 添加表单 | ⚠️ 表单构建占位 |
| 3 | POST | / | store | 保存分类 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改分类 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除分类 | ✅ |

### 3.7 SystemStorageController — 云存储管理

**PHP Prefix**: `ent/config/storage` | **Java**: `@RequestMapping("/ent/config/storage")`  
**状态**: 已有，需补全 4 处占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 云存储列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ⚠️ 占位，需返回 PHP 格式的 elForm 字段定义 |
| 3 | POST | / | store | 保存配置 | ✅ |
| 4 | GET | /config/{id} | config | 配置表单 | ⚠️ 占位，需根据存储类型返回配置字段 |
| 5 | PUT | /config/{id} | updateConfig | 修改配置 | ✅ |
| 6 | PUT | /status/{id} | status | 切换状态 | ✅ |
| 7 | DELETE | /{id} | destroy | 删除 | ✅ |
| 8 | POST | /sync/{id} | sync | 同步云存储 | ⚠️ 占位，需实现：调用 S3 SDK 验证连通性 |
| 9 | GET | /domain/{id} | domain | 域名配置 | ✅ |
| 10 | PUT | /domain/{id} | updateDomain | 修改域名 | ✅ |
| 11 | GET | /domain_form/{id} | domainForm | 域名表单 | ⚠️ 占位，需返回域名配置字段 |

**实现要点**:
- `create` / `config`: PHP 使用表单构建器动态返回字段，Java 侧需按存储类型（本地/OSS/COS/七牛）返回对应 `OaElFormVO`
- `sync`: 用已有 `common-oss` 的 S3 客户端尝试 `listBuckets` 或 `headBucket` 验证

### 3.8 MessageController — 系统消息

**PHP Prefix**: `ent/system/message` | **Java**: `@RequestMapping("/ent/system/message")`  
**状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /list | list | 消息列表（list+count） | ✅ |
| 2 | GET | /cate | cate | 分类 + 未读数 | ✅ |
| 3 | GET | /find/{id} | find | 消息详情 | ✅ |
| 4 | PUT | /{id} | update | 修改消息 | ✅ |
| 5 | PUT | /status/{id} | status | 修改状态 | ✅ |
| 6 | GET | /subscribe | subscribe | 订阅列表 | ⚠️ 需核验 |
| 7 | POST | /sync | sync | 同步模板 | ⚠️ 需核验 |

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
**状态**: 已有，需补全 2 处占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ⚠️ 占位 |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |
| 7 | PUT | /show/{id} | show | 显示/隐藏 | ⚠️ 占位，需实现 `status` 字段切换 |

### 3.11 CategoryController — 快捷分类

**PHP Prefix**: `ent/config/quickCate` | **状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create | create | 表单 | ⚠️ 占位 |
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

**PHP Prefix**: `ent/config` | **状态**: 已有

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /work_bench | getWorkBench | 工作台配置 | ✅ |
| 2 | POST | /work_bench | saveWorkBench | 保存工作台配置 | ✅ |
| 3 | GET | /data/updateConfig | updateConfig | 修改配置表单 | ⚠️ 需核验 |
| 4 | PUT | /data/all/{cate_id} | updateAll | 修改系统配置 | ⚠️ 需核验 |
| 5 | GET | /data/firewall | firewallConfig | 防火墙配置 | ⚠️ 需核验 |
| 6 | PUT | /data/firewall | saveFirewall | 保存防火墙 | ⚠️ 需核验 |

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
| eb_rules | Rules | 需核验 |
| eb_system_attach | SystemAttach | ✅ 已有 |
| eb_system_attach_cate | SystemAttachCategory | ✅ 已有 |
| eb_system_storage | SystemStorage | ✅ 已有 |
| eb_message_category | MessageCategory | ✅ 已有 |
| eb_form_cate | FormCategory | 需核验 |
| eb_form_data | FormData | 需核验 |
| eb_system_quick | SystemQuick | 需核验 |
| eb_quick_cate | QuickCategory | 需核验 |
| eb_agreement | Agreement | ✅ 已有 |
| eb_system_config | SystemConfig | 需核验 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 实现未保存权限接口 | SystemMenusController | P0 |
| 2 | 实现保存企业菜单到超级角色 | SystemMenusController | P0 |
| 3 | 实现超级角色权限修改与权限树 | EnterpriseRoleController | P0 |
| 4 | 消除云存储创建/配置/同步/域名表单占位 | SystemStorageController | P1 |
| 5 | 实现附件内容更新 | SystemAttachController | P2 |
| 6 | 核验企业配置防火墙等接口 | EnterpriseConfigController | P1 |
| 7 | 实现快捷入口创建表单与显示切换 | SystemQuickController | P1 |
| 8 | 实现快捷分类创建表单 | CategoryController | P2 |
| 9 | 核验消息订阅/同步接口 | MessageController | P1 |
