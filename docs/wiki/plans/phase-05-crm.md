# 阶段五：CRM 客户管理 (P1)

> **优先级**: P1  
> **前置依赖**: 阶段一（用户/组织）、阶段二（字典/附件）、阶段四（日程 — 提醒联动）  
> **预估工时**: 3-4 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

前端客户管理全模块可用：**客户列表/详情/流转/统计 → 联系人 → 合同 → 发票 → 付款 → 跟进 → 文件 → 标签 → 提醒**

### 验收检查清单

- [x] 标签 CRUD + 客户列表标签筛选
- [x] 客户提醒 CRUD + 日程联动（创建/删除提醒自动同步日程）
- [x] 客户跟进 CRUD + 附件关联（`relation_type=5`）
- [x] 客户文件上传/删除/重命名
- [ ] 客户主流程：列表/详情/创建/修改/删除/流转/统计/**导入**（统计已对齐 PHP；**导入**仍待与 PHP `FormService`/业务员映射对齐）
- [x] 联系人 CRUD
- [x] 客户记录列表
- [ ] 合同主流程：列表/详情/创建/修改/删除/关注/异常/转移/**导入**（**统计/导入**待对齐，异常/转移待核验）
- [x] 合同附件 CRUD
- [ ] 发票全流程：列表/审核/备注/作废/撤回/开票（**在线开票 URI / callback** 待对接或明确不支持）
- [ ] 付款(Bill)：列表/累计金额/待开票/**合同&客户维度统计**（部分接口路径与 PHP 可能不一致，待对齐）
- [ ] 所有接口与 PHP 同路径、同参数、同响应结构（客户统计类接口已按 PHP 路由对齐；其余模块持续核对中）

---

## 二、模块执行顺序（严格依赖链）

```
1. CrmClientLabelController       — 标签最先（客户列表筛选依赖）
2. CrmClientRemindController      — 提醒（依赖阶段四日程）
3. CrmClientFollowController      — 跟进（含附件 + 日程联动）
4. CrmClientFileController        — 文件
5. CrmCustomerController          — 客户主流程（依赖标签/提醒/跟进）
6. CrmCustomerLiaisonController   — 联系人（依赖客户）
7. CrmCustomerRecordController    — 客户记录
8. CrmContractController          — 合同（依赖客户）
9. CrmContractResourceController  — 合同附件
10. CrmClientInvoiceController    — 发票（依赖合同）
11. CrmClientBillController       — 付款(CRM 维度)
```

> **关键**: 标签 → 提醒（联动日程）→ 跟进/文件 → 客户 → 联系人/记录 → 合同 → 账单/发票

---

## 三、接口清单

### 3.1 CrmClientLabelController — 客户标签

**PHP Prefix**: `ent/client/labels` | **Java**: `@RequestMapping("/ent/client/labels")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 标签列表 | ✅ |
| 2 | POST | / | store | 保存标签 | ✅ |
| 3 | PUT | /{id} | update | 修改标签 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除标签 | ✅ |

### 3.2 CrmClientRemindController — 客户提醒

**PHP Prefix**: `ent/client/remind` | **Java**: `@RequestMapping("/ent/client/remind")`  
**状态**: ✅ 已实现（含日程联动）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 提醒列表 | ✅ |
| 2 | POST | / | store | 保存提醒（自动创建日程） | ✅ |
| 3 | PUT | /{id} | update | 修改提醒 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除提醒（联删日程） | ✅ |
| 5 | PUT | /remark/{id} | remark | 修改备注 | ✅ |
| 6 | PUT | /abandon/{id} | abandon | 放弃提醒 | ✅ |
| 7 | GET | /{id} | detail | 提醒详情 | ✅ |

**已实现联动**: `CrmScheduleSideEffectService` — `saveSchedule`→`updatePeriod` / `delScheduleAfter`→物理删提醒 / 跟进类 `types=0` 清 `uniqued`

### 3.3 CrmClientFollowController — 客户跟进

**PHP Prefix**: `ent/client/follow` | **Java**: `@RequestMapping("/ent/client/follow")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 跟进列表（含 `attachs` 附件） | ✅ |
| 2 | POST | / | store | 保存跟进（`attach_ids` → `FollowAttachRelationService`） | ✅ |
| 3 | PUT | /{id} | update | 修改跟进 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除跟进（联动通知） | ✅ |

### 3.4 CrmClientFileController — 客户文件

**PHP Prefix**: `ent/client/file` | **Java**: `@RequestMapping("/ent/client/file")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 文件列表 | ✅ |
| 2 | DELETE | /{id} | destroy | 删除文件 | ✅ |
| 3 | POST | /upload | upload | 上传文件 | ✅ |
| 4 | PUT | /rename/{id} | rename | 重命名 | ✅ |

### 3.5 CrmCustomerController — 客户主流程

**PHP Prefix**: `ent/client/customer` | **Java**: `@RequestMapping("/ent/client/customer")`  
**状态**: ✅ 已实现并与 PHP 路由对齐；**列表统计**中「急需跟进」与 PHP 日程联表逻辑仍有差异；**导入**、**表单字段全量核验**仍待办

> **说明**：下列 Path 以 **PHP / 当前 Java** 为准（旧文档中的 `/census`、`/performance` 等为笔误，勿与实现对照）。

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | /list | list | 客户列表（POST 带 `customer_label` 筛选） | ✅ |
| 2 | GET | /info/{id} | info | 客户详情（含 `customer_label` ID 数组） | ✅ |
| 3 | POST | / | store | 创建客户 | ✅ |
| 4 | PUT | /{id} | update | 修改客户 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除客户 | ✅ |
| 6 | GET | /list_statistics | listStatistics | 客户列表统计（对应 PHP `list_statistics`） | ✅ 已对齐（`urgent_follow_up` 仍为近似规则） |
| 7 | POST | /lost | lost | 流失客户（对应 PHP `lost`） | ✅ |
| 8 | POST | /return | returnHigh | 退回公海（对应 PHP `return`） | ✅ |
| 9 | POST | /subscribe/{id}/{status} | subscribe | 关注/取消关注 | ✅ |
| 10 | POST | /cancel_lost/{id} | cancelLost | 取消流失（对应 PHP `cancel_lost`） | ✅ |
| 11 | GET | /salesman | salesman | 业务员列表 | ✅ |
| 12 | POST | /claim | claim | 领取客户（对应 PHP `claim`） | ✅ |
| 13 | POST | /label | label | 批量设置标签 | ✅ |
| 14 | POST | /shift | shift | 转移客户（对应 PHP `shift`） | ✅ |
| 15 | POST | /statistics | statistics | 业绩统计（对应 PHP `statistics`） | ✅ 已对齐 PHP 聚合结构 |
| 16 | POST | /contract_rank | contractRank | 合同类型分析（对应 PHP `contract_rank`） | ✅ 已对齐 |
| 17 | POST | /ranking | ranking | 业务员排行榜（对应 PHP `ranking`） | ✅ 已对齐 |
| 18 | POST | /trend_statistics | trendStatistics | 业绩趋势（对应 PHP `trend_statistics`） | ✅ 已对齐 |
| 19 | POST | /import | importRows | 导入客户 | ⚠️ 待与 PHP `batchImport`/表单字段完全对齐 |
| 20 | GET | /create | create | 创建表单 | ⚠️ 待与 PHP `FormService` 逐项核验 |
| 21 | GET | /{id}/edit | edit | 编辑表单 | ⚠️ 待与 PHP `getEditInfo` 逐项核验 |
| — | GET | /select | select | 客户下拉（PHP 另有 `select`） | ✅ |

### 3.6 CrmCustomerLiaisonController — 联系人

**PHP Prefix**: `ent/client/liaisons` | **Java**: `@RequestMapping("/ent/client/liaisons")`  
**状态**: ✅ 已实现（CRUD 接口完整，字段级对照待办）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 联系人列表 | ✅ |
| 2 | POST | / | store | 保存联系人 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 4 | PUT | /{id} | update | 修改联系人 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | GET | /create | create | 创建表单 | ✅ |

### 3.7 CrmCustomerRecordController — 客户记录

**PHP Prefix**: `ent/client/record` | **Java**: `@RequestMapping("/ent/client/record")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 客户记录列表 | ✅ |

### 3.8 CrmContractController — 合同管理

**PHP Prefix**: `ent/client/contracts` | **Java**: `@RequestMapping("/ent/client/contracts")`  
**状态**: 大部分已实现，统计/导入待对齐

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 合同列表 | ✅ |
| 2 | POST | / | store | 创建合同 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 4 | PUT | /{id} | update | 修改 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | GET | /{id} | show | 详情 | ✅ |
| 7 | POST | /census | census | 合同统计 | ⚠️ 需对齐 |
| 8 | PUT | /follow/{id} | follow | 关注 | ✅ |
| 9 | GET | /select | select | 下拉列表 | ✅ |
| 10 | PUT | /abnormal/{id} | abnormal | 异常状态 | ⚠️ 需核验 |
| 11 | PUT | /transfer/{id} | transfer | 转移 | ⚠️ 需核验 |
| 12 | POST | /import | import | 导入 | ⚠️ 需对齐 |
| 13 | GET | /create | create | 创建表单 | ⚠️ 需核验 |

### 3.9 CrmContractResourceController — 合同附件

**PHP Prefix**: `ent/client/resources` | **Java**: `@RequestMapping("/ent/client/resources")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 附件列表 | ✅ |
| 2 | POST | / | store | 上传附件 | ✅ |
| 3 | PUT | /{id} | update | 修改 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |

### 3.10 CrmClientInvoiceController — 发票管理

**PHP Prefix**: `ent/client/invoice` | **Java**: `@RequestMapping("/ent/client/invoice")`  
**状态**: 大部分已实现，在线开票 URI 占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 发票列表 | ✅ |
| 2 | POST | / | store | 保存发票 | ✅ |
| 3 | PUT | /{id} | update | 修改发票 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | PUT | /bindPay/{id} | bindPay | 关联付款 | ✅ |
| 6 | PUT | /audit/{id} | audit | 审核 | ✅ |
| 7 | PUT | /remark/{id} | remark | 备注 | ✅ |
| 8 | PUT | /transfer/{id} | transfer | 转移 | ✅ |
| 9 | GET | /void_form/{id} | voidForm | 作废表单 | ✅ |
| 10 | POST | /void/{id} | voidApply | 作废申请 | ✅ |
| 11 | PUT | /void_audit/{id} | voidAudit | 作废审核 | ✅ |
| 12 | GET | /amount | amount | 累计金额 | ✅ |
| 13 | PUT | /revoke/{id} | revoke | 撤回 | ✅ |
| 14 | GET | /{id} | detail | 详情 | ✅ |
| 15 | GET | /record/{id} | record | 开票操作记录 | ✅ |
| 16 | GET | /invoice_uri | invoiceUri | 在线开票 URI | ⚠️ 占位，需对接开票网关或返回不支持 |
| 17 | ANY | /callback | callback | 发票回调 | ⚠️ 需核验 |

### 3.11 CrmClientBillController — CRM 付款

**PHP Prefix**: `ent/client/bill` | **Java**: `@RequestMapping("/ent/client/bill")`  
**状态**: ✅ 大部分已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 付款列表 | ✅ |
| 2 | POST | / | store | 保存付款 | ✅ |
| 3 | GET | /amount | amount | 累计金额 | ✅ |
| 4 | GET | /pending | pending | 待开票 | ✅ |
| 5 | POST | /contract_census | contractCensus | 合同统计 | ⚠️ 需对齐 |
| 6 | POST | /customer_census | customerCensus | 客户统计 | ⚠️ 需对齐 |
| 7 | DELETE | /{id} | destroy | 删除 | ✅ |

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_customer | Customer | ✅ 已有 |
| eb_client_liaison | CustomerLiaison | ✅ 已有 |
| eb_contract | Contract | 需核验 |
| eb_client_invoice | ClientInvoice | ✅ 已有 |
| eb_client_invoice_log | ClientInvoiceLog | ✅ 已有 |
| eb_client_bill | ClientBill | 需核验 |
| eb_client_bill_log | ClientBillLog | ✅ 已有 |
| eb_client_follow | ClientFollow | ✅ 已有 |
| eb_client_file | ClientFile | 需核验 |
| eb_client_label | ClientLabel | 需核验 |
| eb_client_remind | ClientRemind | ✅ 已有 |
| eb_client_record | ClientRecord | 需核验 |
| eb_contract_resource | ContractResource | 需核验 |
| eb_follow_attach_relation | FollowAttachRelation | 需核验 |

---

## 五、本阶段待办汇总

**状态图例**：**✅ 已完成** · **⏳ 待办**（含未开始/进行中）

| 序号 | 任务 | 控制器 | 优先级 | 状态 |
|------|------|--------|--------|------|
| 1 | 对齐客户统计（`list_statistics` / `statistics` / `contract_rank` / `ranking` / `trend_statistics`） | CrmCustomerController | P1 | **✅ 已完成**（`list_statistics` 中 `urgent_follow_up` 与 PHP 日程联表仍有差异，见 §3.5） |
| 2 | 对齐客户导入功能 | CrmCustomerController | P1 | **⏳ 待办** |
| 3 | 核验客户创建/编辑表单 | CrmCustomerController | P1 | **⏳ 待办** |
| 4 | 对齐合同统计/导入 | CrmContractController | P1 | **⏳ 待办** |
| 5 | 核验合同异常状态/转移 | CrmContractController | P1 | **⏳ 待办** |
| 6 | 处理发票在线开票 URI（对接或明确不支持） | CrmClientInvoiceController | P2 | **⏳ 待办** |
| 7 | 对齐 CRM 付款合同/客户统计 | CrmClientBillController | P1 | **⏳ 待办** |
| 8 | 逐项对照联系人/记录字段与 PHP | CrmCustomerLiaisonController | P2 | **⏳ 待办** |
| 9 | 核验 `getRenewCensus` 等复杂统计 | 多个控制器 | P2 | **⏳ 待办** |
