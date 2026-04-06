# 阶段四：OA 办公与工作流 (P1)

> **优先级**: P1  
> **前置依赖**: 阶段一（用户/组织）、阶段二（字典/权限/附件）  
> **预估工时**: 3-4 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端考勤、审批、日程、日报、备忘录模块完整可用；工作流引擎可支撑审批流程。

### 验收检查清单

- [ ] 考勤：考勤组 CRUD、班次管理、排班、周期、打卡记录、统计报表全流程
- [ ] 审批：审批配置 CRUD、审批申请全流程（提交→处理→撤销→催办→加签→转审→导出）
- [ ] 假期类型管理、审批评价
- [ ] 日程：日程类型 CRUD + 日程 CRUD + 评价 + 分页
- [ ] 日报：日报 CRUD + 下级人员 + 回复 + 统计 + 导出
- [ ] 备忘录：CRUD + 分组
- [ ] 工作流：`bubble-biz-flow` 至少提供审批流转基础能力（可先 PHP 等价直实现）
- [ ] 本阶段全部占位桩清零（AttendanceController 12处、ApproveApplyController 8处、DailyController 8处、ReportController、ScheduleController）

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
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 班次列表 | ❌ 需新增 |
| 2 | POST | / | store | 保存班次 | ❌ 需新增 |
| 3 | PUT | /{id} | update | 修改班次 | ❌ 需新增 |
| 4 | DELETE | /{id} | destroy | 删除 | ❌ 需新增 |
| 5 | GET | /select | select | 下拉列表 | ❌ 需新增 |
| 6 | GET | /{id} | detail | 班次详情 | ❌ 需新增 |

**实现要点**: 查 `eb_attendance_shift`，班次含上下班时间组、弹性时间、加班规则等 JSON 字段

### 3.5 AttendanceArrangeController — 排班管理

**PHP Prefix**: `ent/attendance/arrange` | **Java**: `@RequestMapping("/ent/attendance/arrange")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 排班列表 | ❌ 需新增 |
| 2 | POST | / | store | 保存排班 | ❌ 需新增 |
| 3 | PUT | /{id} | update | 修改排班 | ❌ 需新增 |
| 4 | GET | /info/{group_id} | info | 排班详情 | ❌ 需新增 |

### 3.6 RosterCycleController — 排班周期

**PHP Prefix**: `ent/attendance/cycle` | **Java**: `@RequestMapping("/ent/attendance/cycle")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 周期列表 | ❌ 需新增 |
| 2 | POST | / | store | 保存周期 | ❌ 需新增 |
| 3 | PUT | /{id} | update | 修改周期 | ❌ 需新增 |
| 4 | DELETE | /{id} | destroy | 删除 | ❌ 需新增 |
| 5 | GET | /{id} | detail | 周期详情 | ❌ 需新增 |
| 6 | GET | /arrange/{id} | arrange | 周期排班列表 | ❌ 需新增 |

### 3.7 AttendanceController — 考勤统计与打卡（占位清零重点）

**PHP Prefix**: `ent/attendance` | **Java**: `@RequestMapping("/ent/attendance")`  
**状态**: 全量占位，12 处需替换为真实逻辑

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 打卡记录列表 | ⚠️ 占位空列表，需实现：查 `eb_attendance_clock` 分页 |
| 2 | GET | /daily | daily | 每日统计 | ⚠️ 占位，需实现：按日聚合出勤/迟到/早退/缺勤/加班 |
| 3 | GET | /monthly | monthly | 月度统计 | ⚠️ 占位，需实现：按月聚合 |
| 4 | GET | /presence | presence | 出勤统计 | ⚠️ 占位，需实现：全员出勤率 |
| 5 | GET | /personal | personal | 个人统计 | ⚠️ 占位，需实现：当前用户考勤汇总 |
| 6 | GET | /clock | clock | 打卡记录 | ⚠️ 占位，需实现 |
| 7 | GET | /clock/{id} | clockDetail | 打卡详情 | ⚠️ 占位，需实现 |
| 8 | GET | /anomaly | anomaly | 异常记录列表 | ⚠️ 占位，需实现：筛选迟到/早退/缺卡 |
| 9 | POST | /import_clock | importClock | 导入打卡 | ⚠️ 占位，需实现：Excel 解析 → 写 `eb_attendance_clock` |
| 10 | POST | /import_third | importThird | 导入三方打卡 | ⚠️ 占位，需实现 |
| 11 | POST | /handle | handle | 添加处理记录 | ⚠️ 占位，需实现：写 `eb_attendance_statistics` 处理记录 |
| 12 | GET | /handle/{id} | handleRecord | 处理记录 | ⚠️ 占位，需实现 |

**实现要点**:
- 统计需联查 `eb_attendance_group` + `eb_attendance_shift` + `eb_attendance_clock` 计算迟到/早退/缺勤
- PHP 的 `AttendanceStatisticsService` 含复杂的打卡时间与班次时间比对逻辑，需精确复刻
- 导入走 `common-excel` 模块

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
**状态**: 已有，8 处占位需消除

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
| eb_attendance_shift | AttendanceShift | 需新增 |
| eb_attendance_arrange | AttendanceArrange | 需新增 |
| eb_attendance_clock | AttendanceClock | 需新增 |
| eb_attendance_statistics | AttendanceStatistics | 需新增 |
| eb_roster_cycle | RosterCycle | 需新增 |
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
| 8 | 消除考勤统计全量占位 (12接口) | AttendanceController | P0 |
| 9 | 实现审批类型筛选 | ApproveConfigController | P1 |
| 10 | 消除审批申请全量占位 (8接口) | ApproveApplyController | P0 |
| 11 | 消除日报下级人员/统计占位 (8接口) | DailyController | P0 |
| 12 | 消除工作汇报占位 | ReportController | P1 |
| 13 | 对接 bubble-biz-flow 审批通知 | WorkflowInternalController | P2 |
