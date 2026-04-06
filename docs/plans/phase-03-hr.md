# 阶段三：HR/人事管理 (P1)

> **优先级**: P1  
> **前置依赖**: 阶段一（用户/组织）、阶段二（字典/权限/附件）  
> **预估工时**: 2-3 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端人事模块完整可用：**职级体系管理 → 岗位管理 → 海氏评估 → 晋升管理 → 调薪管理 → 培训管理 → 绩效考核全流程**

### 验收检查清单

- [ ] 职级分类 CRUD、tree 结构正确
- [ ] 职级 CRUD + 创建表单返回 `tree`+`jobInfo`
- [ ] 岗位 CRUD、下拉列表、下级职责读写
- [ ] 职位等级 CRUD、批量修改、关联/取消关联职级
- [ ] 海氏评估组 CRUD、评估数据和历史记录
- [ ] 晋升管理 CRUD + 编辑详情 + 数据表管理
- [ ] 调薪管理含最近记录查询
- [ ] 培训管理读写
- [ ] 绩效考核全流程：方案→计划→模板→指标→自评→上级评→审核评→申诉→统计
- [ ] 所有接口与 PHP 同路径、同参数、同响应结构

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
**状态**: 需核验

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /config | getConfig | 积分配置 | ⚠️ 需核验 |
| 2 | PUT | /config | saveConfig | 保存积分配置 | ⚠️ 需核验 |
| 3 | GET | /audit_config | auditConfig | 审核配置 | ⚠️ 需核验 |

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
**状态**: 部分实现，核心全流程待补

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /index | index | 绩效考核列表 | ⚠️ 需实现：查 `eb_assess` + 关联用户，PHP `AssessService::getList` |
| 2 | GET | /list | list | 人事绩效列表 | ⚠️ 需实现：HR 视角全员列表，多条件分页 |
| 3 | GET | /info/{id} | info | 考核详情 | ⚠️ 需实现：聚合考核+指标+评分+流水 |
| 4 | POST | /create | create | 创建考核 | ⚠️ 需实现：写 `eb_assess` + 关联指标 + 初始流水 |
| 5 | POST | /target | createTarget | 创建考核模板 | ⚠️ 需实现 |
| 6 | POST | /update/{id} | update | 修改考核 | ⚠️ 需实现 |
| 7 | PUT | /self_eval/{id} | selfEval | 自评 | ⚠️ 需实现：写 `eb_assess_user_score` types=0 |
| 8 | PUT | /superior_eval/{id} | superiorEval | 上级评价 | ⚠️ 需实现：校验权限→写评分 |
| 9 | PUT | /examine_eval/{id} | examineEval | 上上级审核 | ⚠️ 需实现 |
| 10 | GET | /show/{id} | show | 启用/禁用（写 `is_show`/`make_status`/`status`） | ✅ |
| 11 | GET | /explain/{id} | explain | 绩效其他信息 | ⚠️ 需实现 |
| 12 | POST | /census | census | 考核统计图 | ⚠️ 需实现：聚合统计，对齐 PHP `getCensus` |
| 13 | POST | /census_bar | censusBar | 人事考核统计图 | ⚠️ 需实现 |
| 14 | PUT | /eval | evalTarget | 指标自评 | ⚠️ 需实现：`data` 数组批量写入 |
| 15 | GET | /score/{id} | record | 评分记录（types=0） | ✅ |
| 16 | GET | /del_form/{id} | deleteForm | 删除表单 | ⚠️ 需实现 |
| 17 | DELETE | /delete/{id} | delete | 删除（需 body.mark + 写删除流水） | ✅ |
| 18 | GET | /del_record | deleteRecord | 删除记录（types=1） | ✅ |
| 19 | POST | /appeal/{id} | appeal | 申诉/驳回 | ⚠️ 需实现：写 `eb_assess_appeal` |
| 20 | GET | /abnormal | abnormal | 未创建考核列表 | ⚠️ 需实现 |
| 21 | GET | /is_abnormal | isAbnormal | 是否存在未创建 | ⚠️ 需实现 |

**实现要点**:
- `create`: PHP `AssessService::create` 包含指标关联、初始评分记录、状态机初始化，需完整复刻
- `selfEval`/`superiorEval`/`examineEval`: 状态流转链 — 自评(1)→上级评(2)→审核评(3)→完成(4)
- `census`/`censusBar`: PHP 含 GROUP BY + 日期维度统计，需 `AssessMapper.xml` 自定义 SQL
- `evalTarget`: 接收 `data` 数组，每项含指标 ID + 分数 + 说明，批量写入 `eb_assess_user_score`

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
| eb_assess | Assess | ✅ 已有 |
| eb_assess_plan | AssessPlan | 需核验 |
| eb_assess_target_cate | AssessTargetCategory | ✅ 已有 |
| eb_assess_target | AssessTarget | 需核验 |
| eb_assess_template | AssessTemplate | ✅ 已有 |
| eb_assess_user_score | AssessUserScore | ✅ 已有 |
| eb_assess_score | AssessScore | ✅ 已有 |
| eb_assess_appeal | AssessAppeal | ✅ 已有 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 核验绩效配置接口（积分/审核） | AssessConfigController | P1 |
| 2 | 实现绩效考核列表（个人+人事视角） | AssessController | P0 |
| 3 | 实现绩效考核详情（聚合指标/评分/流水） | AssessController | P0 |
| 4 | 实现创建/修改考核全流程 | AssessController | P0 |
| 5 | 实现自评/上级评/审核评状态流转链 | AssessController | P0 |
| 6 | 实现指标自评批量写入 | AssessController | P0 |
| 7 | 实现考核统计图（census/censusBar） | AssessController | P1 |
| 8 | 实现申诉/驳回功能 | AssessController | P1 |
| 9 | 实现未创建考核异常检测 | AssessController | P1 |
| 10 | 实现删除表单/其他信息接口 | AssessController | P2 |
