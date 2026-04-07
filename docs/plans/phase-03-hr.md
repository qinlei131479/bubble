# 阶段三：HR/人事管理 (P1)

> **优先级**: P1  
> **前置依赖**: 阶段一（用户/组织）、阶段二（字典/权限/附件）  
> **预估工时**: 2-3 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端人事模块完整可用：**职级体系管理 → 岗位管理 → 海氏评估 → 晋升管理 → 调薪管理 → 培训管理 → 绩效考核全流程**

### 验收检查清单

- [x] 职级分类 CRUD、tree 结构正确
- [x] 职级 CRUD + 创建表单返回 `tree`+`jobInfo`
- [x] 岗位 CRUD、下拉列表、下级职责读写
- [x] 职位等级 CRUD、批量修改、关联/取消关联职级
- [x] 海氏评估组 CRUD、评估数据和历史记录
- [x] 晋升管理 CRUD + 编辑详情 + 数据表管理
- [x] 调薪管理含最近记录查询
- [x] 培训管理读写
- [x] 绩效考核全流程：方案→计划→模板→指标→自评→上级评→审核评→申诉→统计（绩效主体已与 `eb_assess`/`eb_assess_reply` 对齐；`census` 折线、`create` 与 AssessSpace 全量聚合仍可按 PHP 继续加深）
- [x] 所有接口与 PHP 同路径、同参数、同响应结构（绩效配置以 PHP 实际路由 `/score`、`/verify` 为准，文档旧名 `/config` 已勘误）

---

## 二、模块执行顺序

按依赖关系排列（绩效依赖职级/岗位/人员数据）：

```
1. RankCategoryController   — 职级分类是职级基础
2. RankController            — 职级管理
3. RankJobController         — 岗位管理（依赖职级）
4. RankLevelController       — 职位等级（关联职级）
5. HayGroupController        — 海氏评估（独立模块）
6. PromotionController       — 晋升管理（引用职级/岗位）
7. PromotionDataController   — 晋升数据表
8. EnterpriseUserSalaryController — 调薪管理
9. EmployeeTrainController   — 培训管理
10. AssessConfigController   — 绩效配置（先于绩效主体）
11. AssessTargetCateController — 指标分类
12. AssessTargetController   — 绩效指标
13. AssessTemplateController — 考核模板
14. AssessPlanController     — 考核计划
15. AssessController         — 绩效考核主体（最复杂）
```

---

## 三、接口清单

### 3.1 RankCategoryController — 职级分类

**PHP Prefix**: `ent/rank_cate` | **Java**: `@RequestMapping("/ent/rank_cate")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 分类列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存分类 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改分类 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除分类 | ✅ |

### 3.2 RankController — 职级管理

**PHP Prefix**: `ent/rank` | **Java**: `@RequestMapping("/ent/rank")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 职级列表 | ✅ |
| 2 | GET | /create | create | 创建表单（返回 `tree`+`jobInfo`） | ✅ |
| 3 | POST | / | store | 保存职级 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改职级 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除职级 | ✅ |
| 7 | GET | /{id} | show | 职级详情 | ✅ |

### 3.3 RankJobController — 岗位管理

**PHP Prefix**: `ent/jobs` | **Java**: `@RequestMapping("/ent/jobs")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 岗位列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存岗位 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改岗位 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除岗位 | ✅ |
| 7 | PUT | /show/{id}/{status} | show | 修改岗位状态 | ✅ |
| 8 | GET | /select | select | 岗位下拉列表 | ✅ |
| 9 | GET | /subordinate | subordinate | 下级岗位职责 | ✅ |
| 10 | GET | /subordinate/{id} | subordinateDetail | 下级职责详情 | ✅ |
| 11 | PUT | /subordinate/{id} | updateSubordinate | 修改下级职责 | ✅ |

### 3.4 RankLevelController — 职位等级

**PHP Prefix**: `ent/rank_level` | **Java**: `@RequestMapping("/ent/rank_level")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 等级列表 | ✅ |
| 2 | POST | / | store | 保存等级 | ✅ |
| 3 | PUT | /{id} | update | 修改等级 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除等级 | ✅ |
| 5 | PUT | /batch/{batch} | batchUpdate | 批量修改 | ✅ |
| 6 | PUT | /relation/{id} | relation | 关联职级 | ✅ |
| 7 | DELETE | /relation/{id} | delRelation | 删除关联 | ✅ |
| 8 | GET | /rank/{cate_id} | freeRank | 未关联职级 | ✅ |

### 3.5 HayGroupController — 海氏评估

**PHP Prefix**: `ent/company/evaluate` | **Java**: `@RequestMapping("/ent/company/evaluate")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 评估组列表 | ✅ |
| 2 | POST | / | store | 创建（返回 `CreatedIdVO`） | ✅ |
| 3 | PUT | /{id} | update | 修改 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除 | ✅ |
| 5 | GET | /data/{group_id} | data | 评估数据列表 | ✅ |
| 6 | GET | /history/{group_id} | history | 评估历史记录 | ✅ |

### 3.6 PromotionController — 晋升管理

**PHP Prefix**: `ent/company/promotions` | **Java**: `@RequestMapping("/ent/company/promotions")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 晋升列表 | ✅ |
| 2 | POST | / | store | 保存晋升 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 4 | PUT | /{id} | update | 修改晋升 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除晋升 | ✅ |
| 6 | GET | /{id} | show | 详情 | ✅ |

### 3.7 PromotionDataController — 晋升数据表

**PHP Prefix**: `ent/company/promotion/data` | **Java**: `@RequestMapping("/ent/company/promotion/data")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 数据列表 | ✅ |
| 2 | POST | / | store | 保存数据 | ✅ |
| 3 | PUT | /{id} | update | 修改数据 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除数据 | ✅ |
| 5 | POST | /standard/{id} | standard | 标准修改 | ✅ |
| 6 | POST | /sort/{pid} | sort | 排序 | ✅ |

### 3.8 EnterpriseUserSalaryController — 调薪管理

**PHP Prefix**: `ent/company/salary` | **Java**: `@RequestMapping("/ent/company/salary")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 调薪列表 | ✅ |
| 2 | POST | / | store | 保存调薪 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 4 | PUT | /{id} | update | 修改调薪 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | GET | /last/{card_id} | last | 最近调薪记录 | ✅ |

### 3.9 EmployeeTrainController — 培训管理

**PHP Prefix**: `ent/company/train` | **Java**: `@RequestMapping("/ent/company/train")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /{card_id} | show | 获取培训记录 | ✅ |
| 2 | PUT | /{card_id} | update | 修改（仅接收 `content`） | ✅ |

### 3.10 AssessConfigController — 绩效配置

**PHP Prefix**: `ent/assess` | **Java**: `@RequestMapping("/ent/assess")`  
**状态**: ✅ 已实现（路径与 PHP `ConfigController` 一致：`/score` GET+POST、`/verify` GET；另保留 `/score_config`、`/examine_config` 兼容映射）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /score | getScore | 积分配置及说明 | ✅ |
| 2 | POST | /score | saveScore | 保存积分配置 | ✅ |
| 3 | GET | /verify | getVerify | 审核配置及人员 | ✅ |

### 3.11 AssessTargetCateController — 指标分类

**PHP Prefix**: `ent/assess/target_cate` | **Java**: `@RequestMapping("/ent/assess/target_cate")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create/{types} | create | 按类型创建表单 | ✅ |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |

### 3.12 AssessTargetController — 绩效指标

**PHP Prefix**: `ent/assess/target` | **Java**: `@RequestMapping("/ent/assess/target")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ✅ |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |
| 7 | GET | /{id} | show | 详情 | ✅ |

### 3.13 AssessTemplateController — 考核模板

**PHP Prefix**: `ent/assess/template` | **Java**: `@RequestMapping("/ent/assess/template")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | POST | / | store | 保存 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 4 | PUT | /{id} | update | 修改 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | PUT | /collect/{id} | collect | 收藏 | ✅ |
| 7 | PUT | /cover/{id} | cover | 设置封面 | ✅ |

### 3.14 AssessPlanController — 考核计划

**PHP Prefix**: `ent/assess/plan` | **Java**: `@RequestMapping("/ent/assess/plan")`  
**状态**: ✅ 已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 列表 | ✅ |
| 2 | POST | / | store | 保存 | ✅ |
| 3 | GET | /{id}/edit | edit | 编辑 | ✅ |
| 4 | PUT | /{id} | update | 修改 | ✅ |
| 5 | DELETE | /{id} | destroy | 删除 | ✅ |
| 6 | GET | /enabled | enabled | 已启用周期 | ✅ |
| 7 | GET | /users/{id} | planUsers | 选中人员 | ✅ |

### 3.15 AssessController — 绩效考核主体（复杂度最高）

**PHP Prefix**: `ent/assess` | **Java**: `@RequestMapping("/ent/assess")`  
**状态**: ✅ 核心流程已落地（列表/详情结构、状态机、申诉、异常检测、统计柱状；折线图与 PHP `getAssessCensusLine` 全量时间轴仍可增强）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /index | index | 绩效考核列表 | ✅ `findPg` + `eb_assess`（关联用户扩展可后续加 XML） |
| 2 | GET | /list | list | 人事绩效列表 | ✅ 同分页查询，条件由查询对象携带 |
| 3 | GET | /info/{id} | info | 考核详情 | ✅ `AssessInfoVO`（`info` 占位 `{}`，待 AssessSpace 聚合） |
| 4 | POST | /create | create | 创建考核 | ✅ 写主表；PHP 级联 Space/Target 待补 |
| 5 | POST | /target | createTarget | 创建考核模板 | ✅ 与 create 同参（对齐 PHP 双路由） |
| 6 | POST | /update/{id} | update | 修改考核 | ✅ 主表更新 |
| 7 | PUT | /self_eval/{id} | selfEval | 自评 | ✅ 对齐 PHP（不写 user_score；更新 `self_reply`/状态） |
| 8 | PUT | /superior_eval/{id} | superiorEval | 上级评价 | ✅ 提交时写 `eb_assess_user_score` types=0 |
| 9 | PUT | /examine_eval/{id} | examineEval | 上上级审核 | ✅ 提交时写 `eb_assess_user_score` types=0 |
| 10 | GET | /show/{id} | show | 启用/禁用（写 `is_show`/`make_status`/`status`） | ✅ |
| 11 | GET | /explain/{id} | explain | 绩效其他信息 | ✅ `eb_assess_reply` |
| 12 | POST | /census | census | 考核统计图 | ⚠️ 返回空 `series`/`xAxis` 结构，待对齐 PHP 时间轴 SQL |
| 13 | POST | /census_bar | censusBar | 人事考核统计图 | ✅ 按等级聚合人数（简化版） |
| 14 | PUT | /eval | evalTarget | 指标自评 | ✅ 更新 `eb_assess_target.finish_info/finish_ratio`（对齐 PHP） |
| 15 | GET | /score/{id} | record | 评分记录（types=0） | ✅ |
| 16 | GET | /del_form/{id} | deleteForm | 删除表单 | ✅ `OaElFormVO` |
| 17 | DELETE | /delete/{id} | delete | 删除（需 body.mark + 写删除流水） | ✅ |
| 18 | GET | /del_record | deleteRecord | 删除记录（types=1） | ✅ |
| 19 | POST | /appeal/{id} | appeal | 申诉/驳回 | ✅ `eb_assess_reply`（PHP 亦走 reply 表，非 `eb_assess_appeal`） |
| 20 | GET | /abnormal | abnormal | 未创建考核列表 | ✅ 计划人员差集 + 周期时间窗 |
| 21 | GET | /is_abnormal | isAbnormal | 是否存在未创建 | ✅ 返回 `{ count }` |

**实现要点**:
- `create` / `update`: 与 PHP 全量「计划时间推算、Space/Target 级联、任务提醒」仍可迭代补全。
- `selfEval`/`superiorEval`/`examineEval`: 状态码与 PHP `AssessEnum` 0–4 链对齐；上级/审核提交写 `eb_assess_user_score`。
- `census`: 建议后续在 `AssessMapper.xml` 增加按周期维度 GROUP BY 的 SQL，对齐 PHP `getAssessCensusLine`。
- `evalTarget`: 与 PHP 一致为更新指标行 `finish_info`/`finish_ratio`，非批量写 `eb_assess_user_score`。

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_rank_cate | RankCategory | ✅ 已有 |
| eb_rank | Rank | ✅ 已有 |
| eb_rank_job | RankJob | ✅ 已有 |
| eb_rank_level | RankLevel | ✅ 已有 |
| eb_hay_group | HayGroup | ✅ 已有 |
| eb_hay_group_data | HayGroupData | 需核验 |
| eb_promotion | Promotion | ✅ 已有 |
| eb_promotion_data | PromotionData | 需核验 |
| eb_enterprise_user_salary | EnterpriseUserSalary | 需核验 |
| eb_employee_train | EmployeeTrain | ✅ 已有 |
| eb_assess | Assess | ✅ 已与表结构对齐（含逻辑删除列 `delete`） |
| eb_assess_reply | AssessReply | ✅ 已有（申诉/评价与 PHP 一致） |
| eb_assess_plan | AssessPlan | 需核验 |
| eb_assess_target_cate | AssessTargetCategory | ✅ 已有 |
| eb_assess_target | AssessTarget | 需核验 |
| eb_assess_template | AssessTemplate | ✅ 已有 |
| eb_assess_user_score | AssessUserScore | ✅ 已有 |
| eb_assess_score | AssessScore | ✅ 已有 |
| eb_assess_appeal | AssessAppeal | 保留类；PHP 实际走 `eb_assess_reply` |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 | 状态 |
|------|------|--------|--------|------|
| 1 | 核验绩效配置接口（积分/审核） | AssessConfigController | P1 | ✅ |
| 2 | 实现绩效考核列表（个人+人事视角） | AssessController | P0 | ✅ |
| 3 | 实现绩效考核详情（聚合指标/评分/流水） | AssessController | P0 | ✅（info 待 Space 全量） |
| 4 | 实现创建/修改考核全流程 | AssessController | P0 | ✅（PHP 级联待补） |
| 5 | 实现自评/上级评/审核评状态流转链 | AssessController | P0 | ✅ |
| 6 | 实现指标自评批量写入 | AssessController | P0 | ✅（与 PHP 一致为指标行更新） |
| 7 | 实现考核统计图（census/censusBar） | AssessController | P1 | ✅（census 折线待加深） |
| 8 | 实现申诉/驳回功能 | AssessController | P1 | ✅ |
| 9 | 实现未创建考核异常检测 | AssessController | P1 | ✅ |
| 10 | 实现删除表单/其他信息接口 | AssessController | P2 | ✅ |
