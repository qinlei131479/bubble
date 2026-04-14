# 阶段八：低代码平台 (P3)

> **优先级**: P3（全阶段中建议**最后**实施，避免拖慢其余 OA 迁移）  
> **前置依赖**: 阶段二（字典/配置）、阶段四（审批 — 低代码审批依赖审批基础流程）  
> **预估工时**: 3-4 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)  
> **实施顺序**: 主计划约定在**阶段 9、阶段 10（主体）之后**再集中做本阶段；阶段 10 中的低代码数据开放（`OpenModuleController`）仍须在本阶段动态数据能力就绪后收尾。

---

## 一、目标与验收标准

前端低代码模块完整可用：**实体管理 → 字段管理 → 表单设计 → 视图配置 → 触发器 → 数据操作 CRUD → 统计看板 → 接口管理 → 审批配置**

> **风险提示**: 这是技术复杂度最高的模块，涉及动态表结构、运行时数据操作、自定义审批流。

### 验收检查清单

- [ ] 数据表管理：列表/树/创建/修改/复制/删除/信息
- [ ] 字段管理：添加/主展示/修改/删除/列表/详情/关联/类型/搜索类型
- [ ] 触发器：类型/聚合/动作/详情/关联数据/列表/保存/修改/状态/删除
- [ ] 表单：字段表单/保存表单/详情
- [ ] 视图：详情/保存
- [ ] 分类：列表/保存/删除
- [ ] 动态实体数据 CRUD（`{name}` 动态段路由）
- [ ] 统计看板 CRUD + 图表数据
- [ ] curl 接口管理（含测试请求）
- [ ] 实体审批配置 CRUD
- [ ] `CrudModuleWave7Controller` 占位桩已替换为真实逻辑
- [ ] 数据字典列表可用

---

## 二、模块执行顺序

```
1. CrudController（数据表管理）       — 基础，所有其他模块依赖
2. CrudController（字段管理）         — 字段定义被表单/视图/触发器依赖
3. CrudController（分类管理）         — 分类被数据表引用
4. CrudController（表单管理）         — 表单被动态模块数据操作依赖
5. CrudController（视图管理）         — 视图被列表展示依赖
6. CrudController（触发器管理）       — 触发器为高级功能
7. ModuleController（动态实体数据）   — 依赖表/字段/表单/视图全部就绪
8. CrudDashboardController           — 看板依赖数据表和字段
9. CrudCurlController                — 接口管理（独立模块）
10. CrudApproveController            — 审批配置（依赖阶段四审批基础）
11. DataDictController               — 数据字典（辅助功能）
```

---

## 三、接口清单

### 3.1 CrudController — 数据表管理 (34 接口)

**PHP Prefix**: `ent/crud` | **Java**: `@RequestMapping("/ent/crud")`  
**状态**: 需全量新增（当前 `CrudModuleWave7Controller` 为占位壳）

#### 3.1.1 数据表 CRUD (7 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 数据表列表 |
| 2 | GET | /tree | tree | 数据表树形结构 |
| 3 | POST | / | store | 创建数据表（含动态建表） |
| 4 | PUT | /{id} | update | 修改数据表 |
| 5 | POST | /copy/{id} | copy | 复制数据表（含结构复制） |
| 6 | DELETE | /{id} | destroy | 删除数据表（含动态删表） |
| 7 | GET | /{id} | info | 数据表信息 |

**实现要点**: `store`/`copy`/`destroy` 涉及动态 DDL — 需通过 `JdbcTemplate` 或 MyBatis 动态 SQL 创建/复制/删除 `eb_crud_data_{name}` 物理表

#### 3.1.2 字段管理 (9 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 8 | POST | /field | addField | 添加字段（含 ALTER TABLE） |
| 9 | PUT | /field/main/{id} | mainField | 设置主展示字段 |
| 10 | PUT | /field/{id} | updateField | 修改字段 |
| 11 | DELETE | /field/{id} | deleteField | 删除字段（含 ALTER TABLE DROP COLUMN） |
| 12 | GET | /field/{crud_id} | fieldList | 字段列表 |
| 13 | GET | /field/detail/{id} | fieldDetail | 字段详情 |
| 14 | GET | /field/relation/{crud_id} | fieldRelation | 关联字段 |
| 15 | GET | /field/types | fieldTypes | 字段类型列表 |
| 16 | GET | /field/search_types | searchTypes | 搜索类型列表 |

**实现要点**: 字段增删改触发 `ALTER TABLE` 操作，需严格校验字段名合法性和类型映射

#### 3.1.3 触发器管理 (10 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 17 | GET | /event/types | eventTypes | 触发器类型列表 |
| 18 | GET | /event/agg_types | aggTypes | 聚合类型列表 |
| 19 | GET | /event/action_types | actionTypes | 动作类型列表 |
| 20 | GET | /event/detail/{id} | eventDetail | 触发器详情 |
| 21 | GET | /event/relation/{crud_id} | eventRelation | 关联数据 |
| 22 | GET | /event/{crud_id} | eventList | 触发器列表 |
| 23 | POST | /event | storeEvent | 保存触发器 |
| 24 | PUT | /event/{id} | updateEvent | 修改触发器 |
| 25 | PUT | /event/status/{id} | eventStatus | 修改触发器状态 |
| 26 | DELETE | /event/{id} | deleteEvent | 删除触发器 |

#### 3.1.4 表单管理 (3 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 27 | GET | /form/fields/{crud_id} | formFields | 字段表单 |
| 28 | POST | /form/{crud_id} | saveForm | 保存表单配置 |
| 29 | GET | /form/{crud_id} | formDetail | 表单详情 |

#### 3.1.5 视图管理 (2 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 30 | GET | /view/{crud_id} | viewDetail | 视图详情 |
| 31 | POST | /view/{crud_id} | saveView | 保存视图配置 |

#### 3.1.6 分类管理 (3 接口)

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 32 | GET | /cate | cateList | 分类列表 |
| 33 | POST | /cate | saveCate | 保存分类 |
| 34 | DELETE | /cate/{id} | deleteCate | 删除分类 |

### 3.2 ModuleController — 动态实体数据操作 (32 接口)

**PHP Prefix**: `ent/crud/module/{name}` | **Java**: `@RequestMapping("/ent/crud/module/{name}")`  
**状态**: 需全量新增（替换 `CrudModuleWave7Controller` 占位）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 动态实体数据列表 |
| 2 | POST | / | store | 创建数据 |
| 3 | GET | /{id} | show | 数据详情 |
| 4 | PUT | /{id} | update | 修改数据 |
| 5 | DELETE | /{id} | destroy | 删除数据 |
| 6 | DELETE | /batch | batchDestroy | 批量删除 |
| 7 | POST | /search | search | 高级搜索 |
| 8 | GET | /view | view | 视图配置 |
| 9 | GET | /form | form | 表单配置 |
| 10 | GET | /create | createForm | 创建表单 |
| 11 | GET | /{id}/edit | editForm | 编辑表单 |
| 12 | POST | /import | import | 导入数据 |
| 13 | POST | /export | export | 导出数据 |
| 14 | POST | /comment/{id} | comment | 添加评论 |
| 15 | GET | /comment/{id} | commentList | 评论列表 |
| 16 | DELETE | /comment/{comment_id} | deleteComment | 删除评论 |
| 17 | GET | /log/{id} | log | 操作日志 |
| 18 | PUT | /transfer/{id} | transfer | 移交 |
| 19 | PUT | /share/{id} | share | 共享 |
| 20-32 | ... | ... | ... | 问卷/统计/筛选/排序等其余接口 |

**实现要点**:
- 所有操作基于 `{name}` 动态路由，需运行时查 `eb_system_crud` 获取表名 → 动态拼接 SQL
- 数据 CRUD 操作 `eb_crud_data_{name}` 动态表，字段定义从 `eb_system_crud_field` 读取
- 搜索/筛选需动态构建 WHERE 条件

### 3.3 CrudDashboardController — 统计看板

**PHP Prefix**: `ent/crud/dashboard` | **Java**: `@RequestMapping("/ent/crud/dashboard")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 看板列表 |
| 2 | POST | / | store | 保存看板 |
| 3 | PUT | /{id} | update | 修改看板 |
| 4 | DELETE | /{id} | destroy | 删除看板 |
| 5 | GET | /config/{crud_id} | getConfig | 看板配置 |
| 6 | PUT | /config/{crud_id} | saveConfig | 保存看板配置 |
| 7 | POST | /chart/{id} | chart | 图表数据 |
| 8 | POST | /data/{crud_id} | data | 数据列表 |
| 9 | GET | /search_fields/{crud_id} | searchFields | 搜索字段 |

### 3.4 CrudCurlController — 接口管理

**PHP Prefix**: `ent/crud/curl` | **Java**: `@RequestMapping("/ent/crud/curl")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 接口列表 |
| 2 | POST | / | store | 保存接口 |
| 3 | GET | /{id}/edit | edit | 编辑 |
| 4 | PUT | /{id} | update | 修改 |
| 5 | DELETE | /{id} | destroy | 删除 |
| 6 | POST | /test/{id} | test | 测试请求 |
| 7 | POST | /send/{id} | send | 发送请求 |

### 3.5 CrudApproveController — 实体审批配置

**PHP Prefix**: `ent/crud/approve` | **Java**: `@RequestMapping("/ent/crud/approve")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 审批配置列表 |
| 2 | GET | /create | create | 创建表单 |
| 3 | POST | / | store | 保存审批配置 |
| 4 | GET | /{id}/edit | edit | 编辑表单 |
| 5 | PUT | /{id} | update | 修改审批配置 |
| 6 | DELETE | /{id} | destroy | 删除审批配置 |

### 3.6 DataDictController — 数据字典

**PHP Prefix**: `ent/crud` | **Java**: `@RequestMapping("/ent/crud")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /data/list | dataDict | 数据字典列表 |

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_system_crud | SystemCrud | 需新增 |
| eb_system_crud_field | SystemCrudField | 需新增 |
| eb_system_crud_form | SystemCrudForm | 需新增 |
| eb_system_crud_view | SystemCrudView | 需新增 |
| eb_system_crud_event | SystemCrudEvent | 需新增 |
| eb_system_crud_cate | SystemCrudCategory | 需新增 |
| eb_crud_dashboard | CrudDashboard | 需新增 |
| eb_crud_curl | CrudCurl | 需新增 |
| eb_crud_approve | CrudApprove | 需新增 |
| eb_crud_data_dict | CrudDataDict | 需新增 |
| eb_crud_data_{name} | 动态表 | 运行时创建 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 新增全部 10 个实体类 | bubble-api-oa | P0 |
| 2 | 实现数据表 CRUD (7接口) + 动态 DDL | CrudController | P0 |
| 3 | 实现字段管理 (9接口) + ALTER TABLE | CrudController | P0 |
| 4 | 实现表单管理 (3接口) | CrudController | P0 |
| 5 | 实现视图管理 (2接口) | CrudController | P0 |
| 6 | 实现分类管理 (3接口) | CrudController | P1 |
| 7 | 实现触发器管理 (10接口) | CrudController | P1 |
| 8 | 替换 CrudModuleWave7Controller，实现动态数据操作 (32接口) | ModuleController | P0 |
| 9 | 实现统计看板 (9接口) | CrudDashboardController | P1 |
| 10 | 实现接口管理 (7接口) | CrudCurlController | P2 |
| 11 | 实现审批配置 (6接口) | CrudApproveController | P1 |
| 12 | 实现数据字典 (1接口) | DataDictController | P2 |

> **建议**: 此阶段可按子模块拆分迭代 — 先完成表管理+字段管理+表单+视图，再扩展触发器/看板/接口。
