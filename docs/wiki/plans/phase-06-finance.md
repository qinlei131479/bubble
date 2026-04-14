# 阶段六：财务管理 (P2)

> **优先级**: P2  
> **前置依赖**: 阶段五（CRM 合同/客户关联）  
> **预估工时**: 1-1.5 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

前端财务模块完整可用：**财务流水 CRUD/统计/导入 → 流水类别管理 → 支付方式管理**

**当前状态（2026/4/8）**：核心接口与 PHP 对齐已实现；导入与 PHP 同为「请求体 `data` 数组」落库。若需**服务端直接解析 .xlsx**，可后续接入 `bubble-common-excel` 与 PHP 模板对齐。

### 验收检查清单

- [x] 财务流水列表（POST 分页 + 搜索条件）
- [x] 财务流水 CRUD（创建/修改/删除；创建/修改写 `eb_client_bill_log`）
- [x] 统计图（`chart` / `rank_analysis` / `chart_part` 对齐 PHP `getTrend` / `getRankAnalysis` 语义与结构）
- [x] 流水记录查看（`GET /record/{id}` → `eb_client_bill_log`）
- [x] 批量导入财务数据（`POST /import`，`data` 数组；支持支付方式名映射、分类 id/名称及自动建类，对齐 PHP `saveBill`）
- [x] 流水类别 CRUD（含 `OaElFormVO` 创建/编辑表单）
- [x] 支付方式 CRUD（含创建表单 `OaElFormVO`）
- [x] 本阶段 BillController 原占位接口（chart、rank_analysis、chart_part、create、edit、import）已全部落地

---

## 二、模块执行顺序

```
1. BillCategoryController  — 流水类别（被流水列表筛选依赖）
2. PayTypeController        — 支付方式（被流水创建依赖）
3. BillController           — 财务流水主体（依赖上述两个）
```

---

## 三、接口清单

### 3.1 BillCategoryController — 流水类别

**PHP Prefix**: `ent/bill_cate` | **Java**: `@RequestMapping("/ent/bill_cate")`  
**状态**: ✅ 表单已对齐 `OaElFormVO`（`createForm` / `editForm`，支持 `entid` 查询参数）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | page | 类别列表 | ✅ |
| 2 | GET | /create | createForm | 创建表单 | ✅ |
| 3 | POST | / | store | 保存类别 | ✅ |
| 4 | GET | /{id}/edit | editForm | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改类别 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除类别 | ✅ |

**实现要点**: PHP 表单构建器返回 `OaElFormVO` 格式，Java 侧需构建等价的字段定义（`name`/`pid`/`sort` 等）

### 3.2 PayTypeController — 支付方式

**PHP Prefix**: `ent/pay_type` | **Java**: `@RequestMapping("/ent/pay_type")`  
**状态**: ✅ 创建表单已对齐 `OaElFormVO`

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | page | 支付方式列表 | ✅ |
| 2 | GET | /create | createForm | 创建表单 | ✅ |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单数据 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |
| 7 | GET | /{id} | show | 详情 | ✅ |

### 3.3 BillController — 财务流水主体

**PHP Prefix**: `ent/bill` | **Java**: `@RequestMapping("/ent/bill")`  
**状态**: ✅ 统计、占比、表单、导入均已实现（与 PHP 路由及载荷结构对齐）

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | /list | postList | 财务流水列表（POST 分页，含多条件搜索） | ✅ |
| 2 | POST | / | store | 创建流水 | ✅ |
| 3 | PUT | /{id} | update | 修改流水 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除流水 | ✅ |
| 5 | GET | /record/{id} | logs | 流水操作记录（查 `eb_client_bill_log`） | ✅ |
| 6 | POST | /chart | chart | 统计图（`getTrend` all=true：`xAxis`/`series`/`incomeRank`/`expendRank`） | ✅ |
| 7 | POST | /rank_analysis | rankAnalysis | 占比分析（`List<FinanceBillRankRowVO>`，对齐 `getRankAnalysis`） | ✅ |
| 8 | POST | /chart_part | chartPart | 统计数据（`getTrend` all=false，仅排行） | ✅ |
| 9 | GET | /create | createForm | 创建表单 `OaElFormVO`（级联分类、支付方式、金额等） | ✅ |
| 10 | GET | /{id}/edit | editForm | 编辑表单 `OaElFormVO`（含当前值，`entid` 参数） | ✅ |
| 11 | POST | /import | importBill | 导入（请求体 `data` 数组写 `eb_bill_list`，对齐 PHP `saveBill`） | ✅ |

**实现要点**:
- `chart` / `chart_part`: 对齐 PHP `BillService::getTrend`（按区间天数选用小时/`%m-%d`/`%Y-%m` 聚合；`series` 为收入/支出柱状，与 PHP 一致）
- `rank_analysis`: 对齐 PHP `getRankAnalysis`（按分类维度汇总 + 占比）
- `create`/`edit` 表单: `OaElFormVO`，关联 `BillCategory` 级联与 `EnterprisePaytype` 下拉
- `import`: 与 PHP 一致为 JSON `data` 批量写入；**非**服务端 xlsx 解析（可选后续：`bubble-common-excel`）

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_bill_list | BillList | ✅ 已有 |
| eb_bill_category | BillCategory | ✅ 已有 |
| eb_enterprise_paytype | EnterprisePaytype | ✅ 已有 |
| eb_client_bill_log | ClientBillLog | ✅ 已有 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 | 状态 |
|------|------|--------|--------|------|
| 1 | 实现流水类别创建/编辑表单 | BillCategoryController | P1 | ✅ 已完成 |
| 2 | 实现支付方式创建表单 | PayTypeController | P1 | ✅ 已完成 |
| 3 | 实现财务统计图（chart） | BillController | P0 | ✅ 已完成 |
| 4 | 实现占比分析（rank_analysis） | BillController | P0 | ✅ 已完成 |
| 5 | 实现统计数据（chart_part） | BillController | P1 | ✅ 已完成 |
| 6 | 实现创建/编辑表单（含下拉关联） | BillController | P0 | ✅ 已完成 |
| 7 | 实现批量导入财务数据（与 PHP `data` 数组一致） | BillController | P1 | ✅ 已完成 |

**可选后续**：服务端 `.xlsx` 解析上传、与 PHP 静态模板 `bill_import_temp.xlsx` 完全同列校验；前端 `form-create` 控件类型与 Bubble-UI 联调微调。
