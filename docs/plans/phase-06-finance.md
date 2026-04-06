# 阶段六：财务管理 (P2)

> **优先级**: P2  
> **前置依赖**: 阶段五（CRM 合同/客户关联）  
> **预估工时**: 1-1.5 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端财务模块完整可用：**财务流水 CRUD/统计/导入 → 流水类别管理 → 支付方式管理**

### 验收检查清单

- [ ] 财务流水列表（POST 分页 + 搜索条件）
- [ ] 财务流水 CRUD（创建/修改/删除含日志记录）
- [ ] 统计图（chart/rank_analysis/chart_part）与 PHP 数据对齐
- [ ] 流水记录查看
- [ ] Excel 导入财务数据
- [ ] 流水类别 CRUD
- [ ] 支付方式 CRUD
- [ ] 本阶段 BillController 5 处占位桩全部消除

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
**状态**: 已有，2 处表单占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 类别列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ⚠️ 占位，需返回类别表单字段定义 |
| 3 | POST | / | store | 保存类别 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ⚠️ 占位，需返回含当前值的表单 |
| 5 | PUT | /{id} | update | 修改类别 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除类别 | ✅ |

**实现要点**: PHP 表单构建器返回 `OaElFormVO` 格式，Java 侧需构建等价的字段定义（`name`/`pid`/`sort` 等）

### 3.2 PayTypeController — 支付方式

**PHP Prefix**: `ent/pay_type` | **Java**: `@RequestMapping("/ent/pay_type")`  
**状态**: 已有，1 处表单占位

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 支付方式列表 | ✅ |
| 2 | GET | /create | create | 创建表单 | ⚠️ 占位 |
| 3 | POST | / | store | 保存 | ✅ |
| 4 | GET | /{id}/edit | edit | 编辑表单 | ✅ |
| 5 | PUT | /{id} | update | 修改 | ✅ |
| 6 | DELETE | /{id} | destroy | 删除 | ✅ |
| 7 | GET | /{id} | show | 详情 | ✅ |

### 3.3 BillController — 财务流水主体

**PHP Prefix**: `ent/bill` | **Java**: `@RequestMapping("/ent/bill")`  
**状态**: 已有，5 处占位需消除

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | POST | /list | list | 财务流水列表（POST 分页，含多条件搜索） | ✅ |
| 2 | POST | / | store | 创建流水 | ✅ |
| 3 | PUT | /{id} | update | 修改流水 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除流水 | ✅ |
| 5 | GET | /record/{id} | record | 流水操作记录（查 `eb_client_bill_log`） | ✅ |
| 6 | POST | /chart | chart | 统计图 | ⚠️ 占位壳，需实现：按日期/类型 GROUP BY 聚合，对齐 PHP `BillService::getTrend` |
| 7 | POST | /rank_analysis | rankAnalysis | 占比分析 | ⚠️ 占位，需实现：按类别/支付方式统计占比，对齐 PHP `getRankAnalysis` |
| 8 | POST | /chart_part | chartPart | 统计数据 | ⚠️ 占位壳，需实现 |
| 9 | GET | /create | create | 创建表单 | ⚠️ 占位，需返回 `OaElFormVO`（类别下拉+支付方式下拉+金额+备注等字段） |
| 10 | GET | /{id}/edit | edit | 编辑表单 | ⚠️ 占位，需返回含当前值的表单 |
| 11 | POST | /import | import | 导入财务数据 | ⚠️ 需实现：Excel 解析 → 写 `eb_bill_list` |

**实现要点**:
- `chart`: PHP `BillService::getTrend` 按月份聚合收入/支出，返回折线图数据结构 `{month, income, expense}`
- `rank_analysis`: PHP `getRankAnalysis` 按 `bill_cate_id` GROUP BY 统计各类别金额占比
- `chart_part`: 与 `chart` 类似但维度不同（按支付方式或客户维度）
- `create`/`edit` 表单: 需关联 `BillCategory` 和 `PayType` 的下拉选项
- `import`: 使用 `common-excel` 模块，模板与 PHP 导入模板对齐

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

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 实现流水类别创建/编辑表单 | BillCategoryController | P1 |
| 2 | 实现支付方式创建表单 | PayTypeController | P1 |
| 3 | 实现财务统计图（chart） | BillController | P0 |
| 4 | 实现占比分析（rank_analysis） | BillController | P0 |
| 5 | 实现统计数据（chart_part） | BillController | P1 |
| 6 | 实现创建/编辑表单（含下拉关联） | BillController | P0 |
| 7 | 实现 Excel 导入财务数据 | BillController | P1 |
