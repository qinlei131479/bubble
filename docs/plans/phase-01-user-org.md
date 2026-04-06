# 阶段一：核心用户与组织管理 (P0)

> **优先级**: P0 — 全系统基础，所有后续阶段依赖  
> **前置依赖**: 无  
> **预估工时**: 2-3 周（不含下文「暂不开发」范围时可酌情缩短）  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 暂不开发范围（本阶段明确排除）

以下能力**本阶段不开发**，接口可保持占位或继续返回「尚未实现」，待产品需要时再排期：

| 类别 | 说明 |
|------|------|
| 用户注册 | `POST /register` 及注册链路数据写入、企业自动关联等 |
| 扫码登录 | `GET /scan_key`、`POST /scan_status` 及 Redis 扫码会话、移动端确认发 Token |
| 手机验证码登录 | `POST /phone_login` 及短信校验后颁发 Token |
| 强相关公共能力 | 短信发送与发送 key：`POST /verify`、`GET /verify/key`（主要为注册/手机登录服务） |

**仍在本阶段**：账号密码登录、修改密码、`/info`/`/logout`、工作台、组织与通讯录、档案与履历等（与上表无强依赖的部分）。

---

## 一、目标与验收标准

前端能完成完整流程：**账号密码登录 → 获取菜单 → 显示工作台 → 查看/编辑用户信息 → 组织架构管理 → 通讯录 → 个人简历/经历管理 → 员工档案管理**（注册、手机登录、扫码登录不在本阶段验收范围内）

### 验收检查清单

- [ ] 账号密码登录 → 获取 Token → 访问受保护接口
- [ ] ~~注册、手机登录、扫码登录全流程可用~~ **（本阶段暂不开发，不验收）**
- [ ] 工作台快捷入口/待办/业绩统计正确展示
- [ ] 组织架构 tree 正确展示，人员 CRUD 可操作
- [ ] 通讯录搜索、tree 联动正常
- [ ] 个人简历/工作经历/教育经历 CRUD 可用
- [ ] 员工档案全量接口（17 个）可用，含入职/转正/离职
- [ ] 备忘录分类 CRUD 正常
- [ ] 所有接口与 PHP 同路径、同参数、同响应结构

---

## 二、模块执行顺序

按依赖关系和前端页面加载顺序排列：

```
1. LoginController        — 账号密码登录是一切前提（注册/扫码/手机登录暂不开发）
2. CommonController       — 验证码/配置/消息等（短信类接口随注册/手机登录一并延后）
3. UserController         — 登录后获取菜单和用户信息
4. WorkbenchController    — 工作台首页
5. CompanyController      — 企业基本信息
6. FrameController        — 组织架构（被员工档案依赖）
7. CompanyUserController  — 组织架构人员
8. CompanyCardController  — 员工档案核心（依赖组织架构）
9. 履历线控制器群         — 工作/教育/任职/备忘录分类
10. JobAnalysisController — 工作分析（可选并行）
11. CompanyPerfectController — 邀请完善信息（依赖档案）
```

---

## 三、接口清单

### 3.1 LoginController

**PHP Prefix**: `ent/user` | **Java**: `@RequestMapping("/ent/user")`  
**Java 类**: `LoginController` (已有)  
**状态**: 部分实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | /login | login | 账号密码登录 | ✅ 已实现 |
| 2 | POST | /register | register | 用户注册 | ⏸️ **暂不开发**（占位可保留） |
| 3 | POST | /phone_login | phoneLogin | 短信验证码登录 | ⏸️ **暂不开发**（占位可保留） |
| 4 | GET | /info | info | 获取用户信息 | ✅ 已实现 |
| 5 | GET | /logout | logout | 退出登录 | ✅ 已实现 |
| 6 | PUT | /savePassword | savePassword | 修改密码 | ✅ 已实现 |
| 7 | PUT | /save_pwd | password | 修改密码(别名) | ✅ 已实现 |
| 8 | PUT | /common/savePassword | savePassword | 修改密码(别名) | ✅ 已实现 |
| 9 | GET | /scan_key | getScanCode | 获取扫码登录二维码 | ⏸️ **暂不开发**（占位可保留） |
| 10 | POST | /scan_status | scanKeyStatus | 获取扫码状态 | ⏸️ **暂不开发**（占位可保留） |

**实现要点**（待恢复开发时参考）:
- `register`: 校验手机号唯一性 → 密码加密 → 写入 `eb_admin` → 创建或关联 `eb_enterprise` → 返回 Token
- `phone_login`: 验证短信码（`eb_sms_record`）→ 查 `eb_admin` by phone → 生成 JWT
- `scan_key`/`scan_status`: Redis 存储扫码 key，TTL 5 分钟；移动端确认后写入 uid

---

### 3.2 CommonController（本阶段范围）

**PHP Prefix**: `ent/common` | **Java**: `@RequestMapping("/ent/common")`  
**Java 类**: `CommonController` (已有)  
**状态**: 部分实现

> 本阶段实现**账号密码登录**及首页直接依赖的接口（注册/手机登录/扫码登录及短信发送见上文「暂不开发范围」）。上传/下载/发票/问卷等归入阶段二（附件存储）。

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /config | getConfig | 获取网站配置 | ✅ 已实现 |
| 2 | GET | /site | siteConfig | 站点配置 | ✅ 已实现 |
| 3 | GET | /captcha | captcha | 获取图片验证码 | ⚠️ 需实现：生成图片验证码→存 Redis→返回 base64 |
| 4 | GET | /aj_captcha | ajcaptcha | 获取滑块验证码 | ❌ 需新增：AJ-Captcha 集成或自定义实现 |
| 5 | POST | /ajcheck | ajcheck | 滑块验证码一次验证 | ❌ 需新增 |
| 6 | POST | /verify | verify | 短信验证码发送 | ⏸️ **暂不开发**（与注册/手机登录一并恢复） |
| 7 | GET | /verify/key | verifyCode | 获取短信发送 key | ⏸️ **暂不开发**（与注册/手机登录一并恢复） |
| 8 | GET | /message | message | 获取消息列表 | ⚠️ 需实现：查 `eb_notice_record` 按 `is_read` 分页 |
| 9 | PUT | /message/{id}/{isRead} | updateMessage | 修改消息状态 | ❌ 需新增 |
| 10 | GET | /auth | auth | 获取授权信息 | ⚠️ 需实现 |
| 11 | GET | /version | getVersion | 获取版本信息 | ❌ 需新增：读取配置返回版本号 |
| 12 | GET | /logout | logout | 退出登录 | ❌ 需新增（与 LoginController 可共享逻辑） |
| 13 | GET | /site_address | getSiteAddress | 获取网址 | ❌ 需新增：读取 `eb_system_config` 返回站点地址 |
| 14 | GET | /city | city | 查找城市数据 | ❌ 需新增：查 `eb_system_city` 三级联动 |

**阶段二补充的接口（此处登记但不在本阶段实现）**:

| HTTP | Path | 说明 | 归属 |
|------|------|------|------|
| GET | /upload_key | 获取上传 token | 阶段二 |
| POST | /upload | 上传文件 | 阶段二 |
| GET | /download_url | 获取下载地址 | 阶段二 |
| ANY | /download | 下载文件 | 阶段二 |
| POST | /initData | 获取默认数据路径 | 阶段二 |
| ANY | /invoice/call_back | 发票回调 | 阶段六 |
| GET | /q/{unique} | 问卷调查中转 | 阶段九 |
| GET | /questionnaire_info/{unique} | 获取问卷表单 | 阶段九 |
| POST | /questionnaire_save/{name}/{unique} | 问卷保存 | 阶段九 |

---

### 3.3 UserController

**PHP Prefix**: `ent/user` | **Java**: `@RequestMapping("/ent/user")`  
**Java 类**: `UserController` (已有)  
**状态**: 部分实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /menus | menus | 获取当前用户菜单 | ✅ 已实现 |
| 2 | GET | /userInfo | userInfo | 获取当前用户信息 | ❌ 需新增：读 `eb_admin` + `eb_admin_info` 聚合返回 |
| 3 | PUT | /userInfo | update | 修改当前用户信息 | ❌ 需新增：更新 `eb_admin` + `eb_admin_info` |
| 4 | POST | /checkpwd | checkPwd | 验证密码规范 | ❌ 需新增：校验密码复杂度规则 |
| 5 | GET | /resume | resume | 获取个人简历 | ❌ 需新增：查 `eb_user_resume` |
| 6 | PUT | /resume_save | resumeSave | 保存个人简历 | ❌ 需新增：写 `eb_user_resume` |

**实现要点**:
- `userInfo` (GET): 注意与 `LoginController.info` 的路径区分，PHP 中 `/userInfo` 和 `/info` 返回内容不同
- `userInfo` (PUT): 同时更新主表和扩展信息表

---

### 3.4 WorkbenchController

**PHP Prefix**: `ent/user/work` | **Java**: `@RequestMapping("/ent/user/work")`  
**Java 类**: `WorkbenchController` 或 `EnterpriseUserDailyController` (已有)  
**状态**: 大部分已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /menus | getFastEntry | 工作台快捷入口 | ✅ 已实现 |
| 2 | POST | /menus | setFastEntry | 保存快捷入口 | ✅ 已实现 |
| 3 | GET | /count | indexCount | 待办数量 | ✅ 已实现 |
| 4 | GET | /daily | daily | 某月计划列表 | ⚠️ 需核验：是否已接入 `EnterpriseUserDailyService` |
| 5 | GET | /pending | pending | 待办列表 | ⚠️ 需核验：查 `eb_user_pending` 分页 |
| 6 | GET | /statistics_type | statisticsType | 业绩统计类型 | ✅ 已实现 |
| 7 | POST | /statistics_type | updateStatisticsType | 修改统计类型 | ✅ 已实现 |
| 8 | GET | /statistics/{types} | statistics | 工作台业绩统计 | ✅ 已实现 |

---

### 3.5 CompanyController

**PHP Prefix**: `ent/company` | **Java**: `@RequestMapping("/ent/company")`  
**Java 类**: `CompanyController` (已有)  
**状态**: 已完成

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /info | entInfo | 当前企业详情 | ✅ 已实现 |
| 2 | PUT | /info | updateEnt | 修改企业详情 | ✅ 已实现 |
| 3 | GET | /quantity/{type} | quantity | 统计数量 | ✅ 已实现 |

---

### 3.6 FrameController

**PHP Prefix**: `ent/config/frame` | **Java**: `@RequestMapping("/ent/config/frame")`  
**Java 类**: `FrameController` (已有)  
**状态**: 已完成

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 组织结构列表 | ✅ 已实现 |
| 2 | GET | /create | create | 创建表单 | ✅ 已实现 |
| 3 | POST | / | store | 保存组织结构 | ✅ 已实现 |
| 4 | GET | /{id}/edit | edit | 修改表单 | ✅ 已实现 |
| 5 | PUT | /{id} | update | 修改组织结构 | ✅ 已实现 |
| 6 | DELETE | /{id} | destroy | 删除组织结构 | ✅ 已实现 |
| 7 | GET | /tree | getTreeFrame | tree 型组织架构 | ✅ 已实现 |
| 8 | GET | /user | getTreeUser | tree 型人员 | ✅ 已实现 |
| 9 | GET | /users/{frameId} | getFrameUser | 部门人员列表 | ✅ 已实现 |
| 10 | GET | /scope | scopeFrames | 管理范围部门 | ✅ 已实现 |

---

### 3.7 CompanyUserController

**PHP Prefix**: `ent/user` | **Java**: `@RequestMapping("/ent/user")`  
**Java 类**: `CompanyUserController` 或 `EnterpriseUserController` (已有)  
**状态**: 大部分已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /list | index | 组织架构人员列表 | ✅ 已实现 |
| 2 | GET | /card/{id} | editUser | 成员信息 | ✅ 已实现 |
| 3 | PUT | /card/{id} | updateUser | 修改成员 | ⚠️ 需核验 |
| 4 | GET | /add_book/tree | getFrameTree | 通讯录 tree | ✅ 已实现 |
| 5 | GET | /add_book/list | addressBook | 通讯录列表 | ✅ 已实现 |

---

### 3.8 CompanyCardController（员工档案核心）

**PHP Prefix**: `ent/company/card` | **Java**: `@RequestMapping("/ent/company/card")`  
**Java 类**: `CompanyCardController` (已有)  
**状态**: 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | / | index | 企业成员列表 | ✅ |
| 2 | GET | /import/temp | importTemplate | 导入模板 | ✅ |
| 3 | GET | /tree | frameTree | 组织架构 tree | ✅ |
| 4 | POST | /save/{type} | save | 创建员工档案 | ✅ |
| 5 | POST | /import | import | 导入用户档案 | ✅ |
| 6 | GET | /info/{id} | edit | 获取名片 | ✅ |
| 7 | PUT | /{id} | update | 修改档案 | ✅ |
| 8 | POST | /entry/{id} | entry | 员工入职 | ✅ |
| 9 | GET | /formal/{id} | formal | 转正表单 | ✅ |
| 10 | PUT | /be_formal/{id} | beFormal | 员工转正 | ✅ |
| 11 | POST | /quit/{id} | quit | 员工离职 | ✅ |
| 12 | GET | /change | cardChange | 人事异动列表 | ✅ |
| 13 | DELETE | /{id} | destroy | 删除档案 | ✅ |
| 14 | DELETE | /batch | batchDestroy | 批量删除 | ✅ |
| 15 | POST | /batch | batchSetFrame | 批量设置部门 | ✅ |
| 16 | GET | /perfect/{id} | sendPerfect | 邀请完善信息 | ✅ |
| 17 | GET | /interview | sendInterview | 邀请面试 | ✅ |

---

### 3.9 履历线控制器群

#### 3.9.1 UserWorkHistoryController — 个人工作经历

**PHP Prefix**: `ent/user/work_history` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 列表 |
| 2 | GET | /create | create | 创建表单 |
| 3 | POST | / | store | 保存 |
| 4 | GET | /{id}/edit | edit | 修改表单 |
| 5 | PUT | /{id} | update | 修改 |
| 6 | DELETE | /{id} | destroy | 删除 |

#### 3.9.2 UserEducationHistoryController — 个人教育经历

**PHP Prefix**: `ent/user/education` | **状态**: ✅ 已实现  
同 3.9.1 的 6 个标准 CRUD 接口。

#### 3.9.3 UserMemorialCategoryController — 备忘录分类

**PHP Prefix**: `ent/user/memorial_cate` | **状态**: ✅ 已实现  
标准 6 个 CRUD + `GET /create/{pid}` 创建子分类。

#### 3.9.4 EnterpriseEducationController — 企业教育经历管理

**PHP Prefix**: `ent/education` | **状态**: ✅ 已实现  
标准 6 个 CRUD 接口。

#### 3.9.5 EnterprisePositionController — 任职经历管理

**PHP Prefix**: `ent/position` | **状态**: ✅ 已实现  
标准 6 个 CRUD 接口。

#### 3.9.6 EnterpriseWorkController — 企业工作经历管理

**PHP Prefix**: `ent/work` | **状态**: ✅ 已实现  
标准 6 个 CRUD 接口。

---

### 3.10 CompanyPerfectController — 邀请完善信息

**PHP Prefix**: `ent/user/perfect` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /index | list | 邀请记录列表 |
| 2 | PUT | /agree/{id} | agree | 同意发送 |
| 3 | PUT | /refuse/{id} | refuse | 拒绝发送 |

---

### 3.11 JobAnalysisController — 工作分析

**PHP Prefix**: `ent/company/job_analysis` | **状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 列表 |
| 2 | GET | /info/{id} | info | 详情 |
| 3 | GET | /mine | mine | 我的工作分析 |
| 4 | PUT | /{id} | update | 修改 |

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 | 说明 |
|------|--------|---------|------|
| eb_admin | Admin | ✅ 已有 | 用户主表 |
| eb_admin_info | AdminInfo | 需核验 | 用户详细信息 |
| eb_enterprise | Enterprise | ✅ 已有 | 企业信息 |
| eb_enterprise_frame | Frame | ✅ 已有 | 组织架构 |
| eb_enterprise_user_work | EnterpriseUserWork | ✅ 已有 | 工作经历 |
| eb_enterprise_user_education | EnterpriseUserEducation | ✅ 已有 | 教育经历 |
| eb_enterprise_user_position | EnterpriseUserPosition | ✅ 已有 | 任职经历 |
| eb_user_card_perfect | UserCardPerfect | ✅ 已有 | 档案完善记录 |
| eb_user_memorial_cate | UserMemorialCategory | ✅ 已有 | 备忘录分类 |
| eb_user_resume | UserResume | ✅ 已有 | 个人简历 |
| eb_user_pending | UserPending | ✅ 已有 | 待办事项 |
| eb_enterprise_user_job_analysis | EnterpriseUserJobAnalysis | ✅ 已有 | 工作分析 |
| eb_system_city | SystemCity | ✅ 已有 | 城市数据 |
| eb_sms_record | SmsRecord | 随短信能力延后 | 短信发送记录（手机登录/注册验证码）；**暂不开发阶段可不建实体与表对接** |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| ~~1~~ | ~~实现用户注册真实逻辑~~ | LoginController | ⏸️ 暂不开发 |
| ~~2~~ | ~~实现手机验证码登录~~ | LoginController | ⏸️ 暂不开发 |
| ~~3~~ | ~~实现扫码登录 key 生成与状态查询~~ | LoginController | ⏸️ 暂不开发 |
| 1 | 实现图片验证码生成 | CommonController | P0 |
| 2 | 实现滑块验证码 (AJ-Captcha) | CommonController | ⏸️ 暂不开发  |
| 3 | ~~实现短信验证码发送~~ | CommonController | ⏸️ 暂不开发（含 `/verify`、`/verify/key`） |
| 4 | 实现消息列表与状态修改 | CommonController | P1 |
| 5 | 实现授权信息/版本/城市接口 | CommonController | P1 |
| 6 | 实现 GET/PUT /userInfo | UserController | P0 |
| 7 | 实现密码规范校验 | UserController | P1 |
| 8 | 实现个人简历读写 | UserController | P1 |
| 9 | 核验工作台 daily/pending 接口 | WorkbenchController | P1 |
| 10 | 核验 PUT /card/{id} 修改成员 | CompanyUserController | P0 |
