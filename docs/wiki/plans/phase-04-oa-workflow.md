# 阶段四：OA 办公与工作流 (P1)

> **优先级**: P1  
> **前置依赖**: 阶段一（用户/组织）、阶段二（字典/权限/附件）  
> **预估工时**: 3-4 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

前端考勤、审批、日程、日报、备忘录模块完整可用；工作流引擎可支撑审批流程。

**完成情况对照**：下列清单为阶段四整体验收口；**截至 2026-04-07** 的里程碑结论与分模块说明见 **「第六节 阶段完成情况汇总」**。第三节接口表仍保留「逐接口」状态，与第六节一并阅读。

### 验收检查清单

- [~] **考勤**：考勤组 CRUD、班次管理、排班、周期、打卡记录、统计报表全流程 — **部分达成**：日历配置、考勤组、班次/排班/周期控制器与实体已落地；统计与打卡由 `AttendanceEntStatisticsController` + `AttendanceEntStatisticsServiceImpl` 承接（原独立占位 `AttendanceController` 已移除以免路由冲突）；日/月统计、打卡、异常、处理记录、导入（异步分块 + 白名单/免打卡分支写 `eb_attendance_clock_record`）已接库表；复杂班次比对与 PHP 全量对齐、三方导入行级映射仍待补
- [~] **审批**：审批配置 CRUD、审批申请全流程（提交→处理→撤销→催办→加签→转审→导出） — **部分达成**：配置/列表/撤销等已有；**通过（status=1）** 时写入 `eb_attendance_apply_record`（对齐 PHP `createRecord` 解析 `eb_approve_content`）、`calcApplyRecordTime` 可对日重算；`verify` 仍偏状态位更新，表单/审批链/催办/加签/转审/导出等多接口仍占位
- [x] **假期类型管理、审批评价** — **已达成**（`ApproveHolidayTypeController`、`ApproveReplyController` 与 API 实体齐备）
- [ ] **日程**：日程类型 CRUD + 日程 CRUD + 评价 + 分页 — **未达成**（仍以第三节 3.1 为准）
- [ ] **日报**：日报 CRUD + 下级人员 + 回复 + 统计 + 导出 — **未达成**（基础 CRUD/回复已有，统计与导出等占位见 3.12）
- [x] **备忘录**：CRUD + 分组 — **已达成**
- [ ] **工作流**：`bubble-biz-flow` 至少提供审批流转基础能力 — **未达成**（仍为启动类 + 内部桩；OA 侧为 `OaFlowBridgeService` 占位）
- [ ] **本阶段全部占位桩清零** — **未达成**（`ApproveApplyController`、`DailyController`、`ScheduleController`、`ReportController` 等仍有多处占位；考勤统计路径已迁移至 `AttendanceEntStatisticsController`，不再以原 12 处占位表计数）

> 图例：`[x]` 整体验收口；`[~]` 部分达成；`[ ]` 未达成。

---

## 二、模块执行顺序

```
1. ScheduleController        — 日程被 CRM 提醒/跟进依赖（阶段五前置）
2. CalendarConfigController   — 考勤日历配置（先于考勤组）
3. AttendanceGroupController  — 考勤组（被打卡/统计依赖）
4. AttendanceShiftController  — 班次管理
5. AttendanceArrangeController — 排班
6. RosterCycleController      — 排班周期
7. AttendanceController       — 考勤统计/打卡/异常（依赖上述全部）
8. ApproveConfigController    — 审批配置（先于审批申请）
9. ApproveHolidayTypeController — 假期类型
10. ApproveApplyController    — 审批申请主流程（最复杂）
11. ApproveReplyController    — 审批评价
12. DailyController           — 日报主体
13. ReportController          — 工作汇报（可与日报合并策略）
14. NotepadController         — 备忘录
15. WorkflowInternalController — 工作流内部接口（bubble-biz-flow）
```

> **关键依赖**: 日程模块必须在 CRM（阶段五）之前完成，因为客户提醒/跟进与日程联动。

---

## 三、接口清单

### 3.1 ScheduleController — 日程管理

**PHP Prefix**: `ent/schedule` | **Java**: `@RequestMapping("/ent/schedule")`  
**状态**: 部分实现，分页桩需消除

#### 3.1.1 日程类型 (6 接口)

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /type | typeList | 日程类型列表 | ⚠️ 需实现：查 `eb_schedule_type` |
| 2 | GET | /type/create | typeCreate | 类型创建表单 | ⚠️ 需实现 |
| 3 | POST | /type | typeStore | 保存类型 | ⚠️ 需实现 |
| 4 | GET | /type/{id}/edit | typeEdit | 类型编辑表单 | ⚠️ 需实现 |
| 5 | PUT | /type/{id} | typeUpdate | 修改类型 | ⚠️ 需实现 |
| 6 | DELETE | /type/{id} | typeDestroy | 删除类型 | ⚠️ 需实现 |

#### 3.1.2 日程 CRUD (7 接口)

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 7 | GET | /page | page | 日程分页列表 | ⚠️ 占位桩，需实现：按月/用户/类型查 `eb_schedule` + `eb_schedule_user` |
| 8 | POST | / | store | 创建日程 | ⚠️ 需实现：写主表 + 参与者 + 可选提醒 |
| 9 | GET | /{id} | show | 日程详情 | ⚠️ 需实现 |
| 10 | PUT | /{id} | update | 修改日程 | ⚠️ 需实现 |
| 11 | DELETE | /{id} | destroy | 删除日程 | ⚠️ 需实现：联删参与者 + 提醒 |
| 12 | PUT | /status/{id} | status | 修改日程状态 | ⚠️ 需实现：参与者完成/退出 |
| 13 | GET | /count | count | 日程数量统计 | ⚠️ 需实现 |

#### 3.1.3 日程评价 (3 接口)

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 14 | POST | /evaluate | evaluate | 提交评价 | ⚠️ 需实现 |
| 15 | GET | /evaluate/{schedule_id} | evaluateList | 评价列表 | ⚠️ 需实现 |
| 16 | DELETE | /evaluate/{id} | evaluateDestroy | 删除评价 | ⚠️ 需实现 |

**实现要点**:
- 日程与 CRM `ClientRemind` 联动：创建/删除日程时触发 `CrmScheduleSideEffectService`
- 参与者状态(`eb_schedule_user`)：`updateStatus` 时同步 `this_period`/`next_period`
- 日程分页需支持 PHP 侧的月度视图、个人/参与视图过滤

### 3.2 CalendarConfigController — 考勤日历配置

**PHP Prefix**: `ent/attendance/calendar` | **Java**: `@RequestMapping("/ent/attendance/calendar")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | detail | 配置详情 | ✅ |
| 2 | POST | / | save | 保存配置 | ✅ |

### 3.3 AttendanceGroupController — 考勤组管理

**PHP Prefix**: `ent/attendance/group` | **Java**: `@RequestMapping("/ent/attendance/group")`  
**状态**: 已有，部分实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 考勤组列表 | ✅ |
| 2 | POST | / | store | 保存考勤组 | ✅ |
| 3 | PUT | /{id} | update | 修改考勤组 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | GET | /{id} | detail | 考勤组详情 | ⚠️ 需核验 |
| 6 | GET | /whitelist/{id} | whitelist | 白名单列表 | ⚠️ 需核验 |
| 7 | PUT | /whitelist/{id} | updateWhitelist | 修改白名单 | ⚠️ 需核验 |
| 8 | GET | /check_repeat | checkRepeat | 重复检测 | ⚠️ 需核验 |
| 9 | GET | /not_join | notJoin | 未参与人员 | ⚠️ 需核验 |
| 10 | GET | /select | select | 下拉列表 | ⚠️ 需核验 |
| 11 | GET | /members/{id} | members | 参加人员 | ⚠️ 需核验 |

### 3.4 AttendanceShiftController — 班次管理

**PHP Prefix**: `ent/attendance/shift` | **Java**: `@RequestMapping("/ent/attendance/shift")`  
**状态**: ✅ 已实现（Java 控制器 + Service + `eb_attendance_shift` / 规则表）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 班次列表 | ✅ |
| 2 | POST | / | store | 保存班次 | ✅ |
| 3 | PUT | /{id} | update | 修改班次 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | GET | /select | select | 下拉列表 | ✅ |
| 6 | GET | /{id} | detail | 班次详情 | ✅ |

**实现要点**: 查 `eb_attendance_shift`，班次含上下班时间组、弹性时间、加班规则等 JSON 字段

### 3.5 AttendanceArrangeController — 排班管理

**PHP Prefix**: `ent/attendance/arrange` | **Java**: `@RequestMapping("/ent/attendance/arrange")`  
**状态**: ✅ 已实现（核心列表/保存/批量/详情对齐 PHP 前缀）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 排班列表 | ✅ |
| 2 | POST | / | store | 保存排班 | ✅ |
| 3 | PUT | /{id} | update | 修改排班 | ✅ |
| 4 | GET | /info/{group_id} | info | 排班详情 | ✅ |

### 3.6 RosterCycleController — 排班周期

**PHP Prefix**: `ent/attendance/cycle` | **Java**: `@RequestMapping("/ent/attendance/cycle")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 周期列表 | ✅ |
| 2 | POST | / | store | 保存周期 | ✅ |
| 3 | PUT | /{id} | update | 修改周期 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | GET | /{id} | detail | 周期详情 | ✅ |
| 6 | GET | /arrange/{id} | arrange | 周期排班列表 | ✅ |

### 3.7 考勤统计与打卡（`/ent/attendance`）

**PHP Prefix**: `ent/attendance` | **Java**: `@RequestMapping("/ent/attendance")`  
**状态**: **已由 `AttendanceEntStatisticsController` 实现主要接口**（日/月统计、出勤/个人汇总、打卡与异常、处理记录、导入异步）；原独立占位 `AttendanceController` 已删除，避免与上列路由冲突。路径名与 PHP 对齐（如 `daily_statistics`、`clock_record`、`clock/import_record` 等）。

| # | HTTP | Path（与 PHP 对齐） | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /daily_statistics | dailyStatistics | 每日统计 | ✅ 已接库表与聚合 |
| 2 | GET | /monthly_statistics | monthlyStatistics | 月度统计 | ✅ 含 `holiday_type` / `holiday_data`（假勤来自 `eb_attendance_apply_record`） |
| 3 | GET | /attendance_statistics | attendanceStatistics | 出勤汇总 | ✅ 假勤时长等同源已汇总 |
| 4 | GET | /individual_statistics | individualStatistics | 个人统计 | ✅ |
| 5 | GET | /clock_record | clockRecord | 打卡记录分页 | ✅ `eb_attendance_clock_record` |
| 6 | GET | /clock_record/{id} | clockRecordInfo | 打卡详情 | ✅ |
| 7 | GET | /abnormal_date | abnormalDate | 异常日期 | ✅ |
| 8 | GET | /abnormal_record/{id} | abnormalRecord | 异常记录列表 | ✅ |
| 9 | POST | /clock/import_record | importRecord | Excel 导入打卡 | ✅ 异步分块 + 写打卡表（与 PHP 队列对齐） |
| 10 | POST | /clock/import_third | importThird | 三方导入 | ⚠️ 异步占位，行级映射待补 |
| 11 | PUT | /statistics/{id} | updateStatistics | 处理记录（补卡结果等） | ✅ 写 `eb_attendance_handle_record` |
| 12 | GET | /statistics/{id} | statisticsRecords | 处理记录 | ✅ |

**实现要点**:
- 统计联查 `eb_attendance_group` + 班次 JSON + `eb_attendance_clock_record`；与 PHP 完全一致的迟到/早退算法仍分模块逐步补齐
- 请假/外出等与审批核算依赖 `eb_attendance_apply_record`；审批通过写入与 `calcApplyRecordTime` 见 `AttendanceApplyRecordSyncService`
- 前端导入可先由前端解析 Excel 为 JSON 再 POST（与 PHP 一致）；服务端异步分块对齐 `AttendanceImportJob`

### 3.8 ApproveConfigController — 审批配置

**PHP Prefix**: `ent/approve/config` | **Java**: `@RequestMapping("/ent/approve/config")`  
**状态**: 已有，1 处占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 审批配置列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存配置 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改配置 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |
| 7 | GET | /search/{types} | search | 筛选列表 | ⚠️ 占位，需实现：按审批类型筛选可用配置 |

### 3.9 ApproveHolidayTypeController — 假期类型

**PHP Prefix**: `ent/approve/holiday_type` | **Java**: `@RequestMapping("/ent/approve/holiday_type")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /{id} | show | 详情 | ✅ |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | PUT | /{id} | update | 修改 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | GET | /select | select | 下拉列表 | ✅ |

### 3.10 ApproveApplyController — 审批申请主流程（占位清零重点）

**PHP Prefix**: `ent/approve/apply` | **Java**: `@RequestMapping("/ent/approve/apply")`  
**状态**: 已有；**已通过审批**时同步写入 `eb_attendance_apply_record`（`AttendanceApplyRecordSyncService`，对齐 PHP `AttendanceApplyRecordService::createRecord`）；`calcApplyRecordTime` 可对日重算；其余接口仍多占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 审批列表 | ✅ |
| 2 | GET | /{id} | show | 审批详情 | ✅ |
| 3 | DELETE | /{id} | destroy | 删除 | ✅ |
| 4 | PUT | /revoke/{id} | revoke | 撤销申请 | ✅ |
| 5 | PUT | /handle/{id} | handle | 处理审批（同意/拒绝/退回） | ⚠️ 占位，需实现：读审批流→校验当前节点→更新 `eb_approve_apply` 状态→写日志→通知 |
| 6 | GET | /form/{approve_id} | form | 审批申请表单 | ⚠️ 占位，需实现：按 `approve_id` 读 `eb_approve_config` 返回 `OaElFormVO` 结构 |
| 7 | GET | /approver/{approve_id} | approver | 审批人员列表 | ⚠️ 占位，需实现：按配置读审批人链 |
| 8 | POST | / | store | 保存审批申请 | ⚠️ 占位，需实现：写 `eb_approve_apply` + 初始化审批流程节点 + 通知首个审批人 |
| 9 | POST | /export | export | 导出审批记录 | ⚠️ 占位，需实现：查询 + Excel 导出 |
| 10 | POST | /urge/{id} | urge | 催办 | ⚠️ 占位，需实现：发送催办通知 |
| 11 | POST | /add_sign/{id} | addSign | 加签 | ⚠️ 占位，需实现：在审批链中插入新审批人 |
| 12 | POST | /transfer/{id} | transfer | 转审 | ⚠️ 占位，需实现：当前审批人转交给指定人 |

**实现要点**:
- `store`: 核心是初始化审批流程 — 读 `eb_approve_config` 的审批人配置 → 按规则构建节点链 → 写 `eb_approve_apply` → 首个节点待审通知
- `handle`: 状态流转 — 待审(0)→同意(1)/拒绝(2)/退回(3)，需考虑会签/或签模式
- **工作流衔接策略**: 先在 `ApproveApplyServiceImpl` 内直接实现 PHP 等价的审批链逻辑；后续迁移到 `bubble-biz-flow` + Flowable/Camunda
- 与 `bubble-biz-flow` 的 `RemoteFlowService` / `OaFlowBridgeService` 在 `create` 后可发占位通知

### 3.11 ApproveReplyController — 审批评价

**PHP Prefix**: `ent/approve/reply` | **Java**: `@RequestMapping("/ent/approve/reply")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | / | store | 保存评价 | ✅ |
| 2 | DELETE | /{id} | destroy | 删除评价 | ✅ |

### 3.12 DailyController — 日报管理（占位清零重点）

**PHP Prefix**: `ent/daily` | **Java**: `@RequestMapping("/ent/daily")`  
**状态**: 部分实现，8 处占位需消除

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 日报列表 | ✅ |
| 2 | POST | / | store | 保存日报 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 4 | PUT | /{id} | update | 修改日报 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除日报 | ✅ |
| 6 | POST | /reply/{id} | reply | 回复日报 | ✅ |
| 7 | DELETE | /reply/{id} | deleteReply | 删除回复 | ✅ |
| 8 | GET | /report_member | reportMember | 下级汇报人员 | ⚠️ 占位空列表，需实现：按组织架构查下属列表 |
| 9 | POST | /submit_census | submitCensus | 提交统计 | ⚠️ 占位，需实现：统计日报提交率 |
| 10 | POST | /report_census | reportCensus | 汇报统计 | ⚠️ 占位，需实现 |
| 11 | GET | /submit_list | submitList | 提交列表 | ⚠️ 占位，需实现：已提交日报的用户列表 |
| 12 | GET | /not_submit_list | notSubmitList | 未提交列表 | ⚠️ 占位，需实现：应提交但未提交的用户列表 |
| 13 | GET | /pending | dailyPending | 日报待办回显 | ⚠️ 占位，需实现：默认汇报人/待审日报 |
| 14 | GET | /report_view | reportView | 汇报人查看列表 | ⚠️ 占位，需实现 |
| 15 | POST | /export | export | 导出日报 | ⚠️ 占位，需实现：Excel 导出 |

**实现要点**:
- `reportMember`: 查组织架构 `eb_enterprise_frame` 递归下属 → 读 `eb_admin` 列表
- `submitCensus`/`reportCensus`: 按日期范围聚合 `eb_enterprise_user_daily` 统计提交率
- 依赖阶段一的组织架构和员工数据一致性

### 3.13 ReportController — 工作汇报

**PHP Prefix**: `ent/daily` (部分路径) | **Java**: `@RequestMapping("/ent/daily")`  
**状态**: 全量占位

> 此控制器可能与 `DailyController` 共用部分路由，以 PHP 前端实际请求为准。

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /report | list | 汇报列表 | ⚠️ 占位空列表，需按 `eb_enterprise_user_daily` 实现分页 |

### 3.14 NotepadController — 备忘录

**PHP Prefix**: `ent/user/memorial` | **Java**: `@RequestMapping("/ent/user/memorial")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | POST | / | store | 保存 | ✅ |
| 3 | PUT | /{id} | update | 修改 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | GET | /group | group | 最新分组列表 | ✅ |

### 3.15 工作流 — bubble-biz-flow

**状态**: 仅启动类 + `WorkflowInternalController` 桩

| # | HTTP | Path | 说明 | 状态 |
|---|------|------|------|------|
| 1 | GET | /internal/workflow/ping | 健康检查 | ✅ 桩 |
| 2 | POST | /internal/workflow/oa-apply-notify | 审批创建通知 | ⚠️ 桩，需对接 |

**工作流实施策略**:
1. **Phase A（本阶段）**: 在 `bubble-biz-oa` 的 `ApproveApplyServiceImpl` 内直接实现 PHP 等价审批链（固定审批人、会签/或签）
2. **Phase B（阶段八之后）**: 引入 Flowable/Camunda，将审批配置映射到 BPMN 流程定义，通过 Feign → `bubble-biz-flow` 调用
3. 两个阶段切换通过 `OaFlowBridgeService` 桥接，对 `bubble-biz-oa` 业务代码无感

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_schedule | Schedule | 需核验 |
| eb_schedule_type | ScheduleType | ✅ 已有 |
| eb_schedule_user | ScheduleUser | ✅ 已有 |
| eb_schedule_evaluate | ScheduleEvaluate | 需新增 |
| eb_attendance_group | AttendanceGroup | 需核验 |
| eb_attendance_group_member | AttendanceGroupMember | ✅ 已有 |
| eb_attendance_group_shift | AttendanceGroupShift | ✅ 已有 |
| eb_attendance_whitelist | AttendanceWhitelist | ✅ 已有 |
| eb_attendance_shift | AttendanceShift | ✅ 已新增 |
| eb_attendance_arrange | AttendanceArrange | ✅ 已新增 |
| eb_attendance_clock_record | AttendanceClockRecord | ✅ 已新增 |
| eb_attendance_statistics | AttendanceStatistics | ✅ 已新增 |
| eb_attendance_apply_record | AttendanceApplyRecord | ✅ 已新增（审批核算） |
| eb_roster_cycle | RosterCycle | ✅ 已新增 |
| eb_approve_content | ApproveContent | ✅ 已新增（审批申请内容解析） |
| eb_approve_config | Approve | ✅ 已有 |
| eb_approve_apply | ApproveApply | ✅ 已有 |
| eb_approve_holiday_type | ApproveHolidayType | 需核验 |
| eb_approve_reply | ApproveReply | 需核验 |
| eb_enterprise_user_daily | EnterpriseUserDaily | ✅ 已有 |
| eb_enterprise_user_daily_reply | EnterpriseUserDailyReply | 需核验 |
| eb_user_memorial | UserMemorial | ✅ 已有 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 实现日程类型 CRUD (6接口) | ScheduleController | P0 |
| 2 | 消除日程分页桩 + 实现日程 CRUD (7接口) | ScheduleController | P0 |
| 3 | 实现日程评价 (3接口) | ScheduleController | P1 |
| 4 | 新增班次管理控制器 (6接口) | AttendanceShiftController | P0 |
| 5 | 新增排班管理控制器 (4接口) | AttendanceArrangeController | P0 |
| 6 | 新增排班周期控制器 (6接口) | RosterCycleController | P0 |
| 7 | 核验考勤组详情/白名单等 (6接口) | AttendanceGroupController | P0 |
| 8 | 消除考勤统计全量占位（现由 `AttendanceEntStatisticsController` 承接，见 3.7 / 第六节） | `AttendanceEntStatisticsController` | P0 |
| 9 | 实现审批类型筛选 | ApproveConfigController | P1 |
| 10 | 消除审批申请全量占位 (8接口) | ApproveApplyController | P0 |
| 11 | 消除日报下级人员/统计占位 (8接口) | DailyController | P0 |
| 12 | 消除工作汇报占位 | ReportController | P1 |
| 13 | 对接 bubble-biz-flow 审批通知 | WorkflowInternalController | P2 |

---

## 六、阶段完成情况汇总（截至 2026-04-07）

### 6.1 总体结论

| 维度 | 结论 |
|------|------|
| **阶段目标** | 「OA 办公」主干能力已部分落地：**考勤（统计/打卡/导入/排班链路）**、**审批（配置 + 假期类型 + 申请通过写核算表）**、**备忘录** 等可联调；**日程 / 日报 / 工作汇报** 仍以占位与第三节清单为准；**bubble-biz-flow** 尚未承担真实流转。 |
| **整体验收** | 第一节验收清单 **未全部勾选**；其中 `[~]` 表示可演示、可连库，但与 PHP 行为或「占位清零」仍有差距（见下表）。 |
| **建议** | 优先清零 **ScheduleController**、**DailyController** 高优先级接口；审批侧补齐 **handle / store / form** 与流程节点；统计侧在 `AttendanceStatisticsApproveHookImpl` 中落地 PHP 级 `updateAbnormalShiftStatus` / 请假汇总。 |

### 6.2 分模块对照

| 模块 | 结论摘要 |
|------|----------|
| **考勤日历** | `CalendarConfigController` 可用；`CalendarConfigService.dayIsRest` 等已对齐 PHP 语义。 |
| **考勤组** | CRUD 与主流程可用；详情/白名单等接口仍建议按第三节逐项核验。 |
| **班次 / 排班 / 周期** | 控制器、实体、Mapper 已落地；与 PHP 边界条件需持续对齐。 |
| **考勤统计与打卡** | 由 `AttendanceEntStatisticsController` 提供 `/ent/attendance` 下主要接口；假勤统计读 `eb_attendance_apply_record`；导入异步分块。 |
| **审批配置** | `ApproveConfigController` 主流程可用；`/search/{types}` 仍占位。 |
| **假期类型 / 审批评价** | 完整可用。 |
| **审批申请** | 列表/详情/撤销可用；**通过** 时写 `eb_attendance_apply_record` 并可调 `calcApplyRecordTime`；**表单/审批链/催办/加签/转审/导出** 等仍占位。 |
| **日程** | 未实现第三节 3.1 清单。 |
| **日报 / 汇报** | 基础 CRUD/回复部分可用；统计、导出、下级等占位仍多。 |
| **备忘录** | 完整可用。 |
| **工作流** | `bubble-biz-flow` 仍为桩；OA 内 `OaFlowBridgeService` 占位。 |

### 6.3 与第五节「待办」的对应关系（进度）

| 序号 | 任务 | 进度（2026-04-07） |
|------|------|----------------------|
| 1～3 | 日程类型 / 日程 / 评价 | **未开始** |
| 4～6 | 班次 / 排班 / 周期 | **已完成**（见第三节更新） |
| 7 | 考勤组详情等核验 | **进行中**（建议联调核验） |
| 8 | 考勤统计占位 | **已迁移至 `AttendanceEntStatisticsController`**，原表 12 路径已按新路径重写；三方导入等待续作 |
| 9 | 审批类型筛选 | **未开始** |
| 10 | 审批申请占位 | **部分完成**（通过写核算表 + `calcApplyRecordTime`）；其余接口仍占位 |
| 11 | 日报占位 | **未开始** |
| 12 | 工作汇报 | **未开始** |
| 13 | flow 通知 | **未开始** |

### 6.4 关键代码入口（便于评审）

| 能力 | 说明 |
|------|------|
| 考勤统计与打卡 | `AttendanceEntStatisticsController`、`AttendanceEntStatisticsServiceImpl` |
| 导入异步 | `AttendanceClockImportAsyncService`、`AttendanceClockImportChunkExecutor`、`AttendanceClockImportRowHandler` |
| 审批通过 → 核算表 | `AttendanceApplyRecordSyncService#createFromPassedApply`、`ApproveApplyServiceImpl#verifyApply` |
| 按日重算（对齐 PHP） | `AttendanceApplyRecordSyncService#calcApplyRecordTime` |
| 统计与审批占位 hook | `AttendanceStatisticsApproveHook` / `AttendanceStatisticsApproveHookImpl` |
