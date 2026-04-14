# 阶段七：项目管理 (P2)

> **优先级**: P2  
> **前置依赖**: 阶段一（用户/组织）  
> **预估工时**: 2 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

前端项目管理模块完整可用：**项目 CRUD → 任务管理（含子任务/批量/排序/分享）→ 版本管理 → 动态 → 评论 → 附件**

### 验收检查清单

- [x] 项目 CRUD + 下拉列表 + 成员管理 + 详情（成员为 `GET /members?program_id=`，详情为 `GET /info/{id}`）
- [x] 任务 CRUD + 子任务 + 批量更新/删除 + 排序 + 分享（路径/方法见 §3.2）
- [x] 版本管理列表 / 保存 / 下拉（保存为 `POST /{programId}` + `data[]`）
- [x] 项目动态 / 任务动态查看（`GET /program`、`GET /task` + query）
- [x] 任务评论 CRUD
- [x] 项目文件列表 / 删除 / 重命名（`eb_system_attach`，`GET /index` 等）
- [ ] 所有接口与 PHP 同路径、同参数、同响应结构（路径与动词已对齐；细粒度字段/分页参数需联调逐项勾选）

---

## 二、模块执行顺序

```
1. ProgramController          — 项目主体（被任务/版本/动态依赖）
2. ProgramVersionController   — 版本管理（被任务关联）
3. ProgramTaskController      — 任务管理（依赖项目+版本）
4. ProgramDynamicController   — 动态（依赖项目+任务）
5. ProgramTaskCommentController — 评论（依赖任务）
6. ProgramFileController      — 文件（依赖项目）
```

---

## 三、接口清单

### 3.1 ProgramController — 项目管理

**PHP Prefix**: `ent/program`（Spatie `#[Prefix]` + Resource） | **Java**: `@RequestMapping("/ent/program")`  
**状态**: 已实现（与 PHP 同路径；成员、详情为查询参数/子路径，非 `GET /{id}`）

| # | HTTP | Path（相对 prefix） | Java 方法 | 主要参数（与 PHP 对齐） | 状态 |
|---|------|---------------------|----------|-------------------------|------|
| 1 | GET | `/` | index | 分页 + 筛选（列表） | ✅ |
| 2 | POST | `/` | store | body：项目字段 | ✅ |
| 3 | PUT | `/{id}` | update | path `id`，body | ✅ |
| 4 | DELETE | `/{id}` | destroy | path `id` | ✅ |
| 5 | GET | `/select` | select | query：`uid` 等（PHP `types`/`uid`/`admin_uid` 等，Java 侧按需扩展） | ✅ |
| 6 | GET | `/members` | members | query：`program_id`（PHP `program_id`，Java `programId`） | ✅ |
| 7 | GET | `/info/{id}` | info | path `id`（详情，非 Resource 的 `show`） | ✅ |

### 3.2 ProgramTaskController — 任务管理

**PHP Prefix**: `ent/program_task` | **Java**: `@RequestMapping("/ent/program_task")`  
**状态**: 已实现（方法名以 Java 为准；**批量/排序为 POST**，与 PHP 一致）

| # | HTTP | Path（相对 prefix） | Java 方法 | 说明 | 状态 |
|---|------|---------------------|----------|------|------|
| 1 | GET | `/` | index | 任务树列表 + 根级分页；筛选见 `ProgramTaskIndexQuery` / 实体 transient | ✅ |
| 2 | POST | `/` | store | 创建任务，`eb_program_task` + 成员表 | ✅ |
| 3 | PUT | `/{id}` | update | 单字段更新：body 内 `field` + 对应字段（含 `plan_date` 等） | ✅ |
| 4 | DELETE | `/{id}` | destroy | 删除任务及 path 子任务、评论、成员（逻辑删） | ✅ |
| 5 | POST | `/subordinate` | subordinateStore | 保存下级任务，body：`pid`、`name`（PHP 路由名 `subordinate`，非 `/sub/{pid}`） | ✅ |
| 6 | GET | `/info/{id}` | info | 任务详情（含关联展示，与 PHP `info/{id}` 一致） | ✅ |
| 7 | GET | `/select` | select | query：`program_id`、`pid` | ✅ |
| 8 | POST | `/batch` | batchUpdate | body：`program_id`、`version_id`、`pid`、`uid`、`status`、`start_date`、`end_date`、`data[]` | ✅ |
| 9 | POST | `/batch_del` | batchDel | body：`data[]` 任务 ID（PHP 为 `batch_del`，非 `DELETE /batch`） | ✅ |
| 10 | POST | `/sort` | sort | body：`current`、`target` 两个兄弟任务 ID，**交换** `sort`（非整表 ID 数组 PUT） | ✅ |
| 11 | GET | `/share/{ident}` | share | path 为任务 `ident` 字符串（非数字 `id`） | ✅ |

**实现要点**（与 PHP 一致）:
- 层级：`pid`、`path`、`top_id`、`level`；最多 4 级。
- 批量更新、批量删除、排序均在 **Service 层事务**内处理。
- 排序：**仅同级**两节点互换 `sort` 值；与「按 ID 数组重排」的设想不同，以 PHP 为准。

### 3.3 ProgramVersionController — 版本管理

**PHP Prefix**: `ent/program_version` | **Java**: `@RequestMapping("/ent/program_version")`  
**状态**: 已实现

| # | HTTP | Path（相对 prefix） | Java 方法 | 说明 | 状态 |
|---|------|---------------------|----------|------|------|
| 1 | GET | `/` | getVersion | query：`program_id` | ✅ |
| 2 | POST | `/{id}` | setVersion | path `id` = **项目 ID**；body：`data[]`（`id`/`name` 版本行，全量覆盖式保存，含删除未提交的旧版本） | ✅ |
| 3 | GET | `/select` | select | query：`program_id`（0 表示当前用户可见多项目下版本） | ✅ |

### 3.4 ProgramDynamicController — 项目动态

**PHP Prefix**: `ent/program_dynamic` | **Java**: `@RequestMapping("/ent/program_dynamic")`  
**状态**: 已实现（路径为 **`/program`、`/task`**，query 筛选，**非** `program/{id}` 路径参数）

| # | HTTP | Path（相对 prefix） | Java 方法 | 主要 query（与 PHP getMore 对应） | 状态 |
|---|------|---------------------|----------|-----------------------------------|------|
| 1 | GET | `/program` | programDynamic | `uid`、`relation_id`（项目动态时 `types` 固定为项目，由服务端处理）；分页 `Pg` | ✅ |
| 2 | GET | `/task` | taskDynamic | `uid`、`program_id`、`relation_id`；任务动态且 `relation_id>0` 时返回额外 `total_count` | ✅ |

**实现要点**: 任务创建/更新/删除等写入 `eb_program_dynamic`；控制器负责分页查询与 VO 组装。

### 3.5 ProgramTaskCommentController — 任务评论

**PHP Prefix**: `ent/task_comment`（Resource） | **Java**: `@RequestMapping("/ent/task_comment")`  
**状态**: 已实现

| # | HTTP | Path（相对 prefix） | Java 方法 | 说明 | 状态 |
|---|------|---------------------|----------|------|------|
| 1 | GET | `/` | index | query：**必填** `task_id` | ✅ |
| 2 | POST | `/` | store | body：`pid`、`task_id`、`describe`、`reply_uid` | ✅ |
| 3 | PUT | `/{id}` | update | path `id`，body | ✅ |
| 4 | DELETE | `/{id}` | destroy | path `id` | ✅ |

### 3.6 ProgramFileController — 项目文件

**PHP Prefix**: `ent/program_file` | **Java**: `@RequestMapping("/ent/program_file")`  
**状态**: 已实现（PHP 挂载在 **附件表** `eb_system_attach`，`relation_type = 9`，非独立 `eb_program_file` 表）

| # | HTTP | Path（相对 prefix） | Java 方法 | 主要参数 | 状态 |
|---|------|---------------------|----------|----------|------|
| 1 | GET | `/index` | index | query：`program_id`（→ `relation_id`）、`entid`、`name`；分页 `Pg` | ✅ |
| 2 | DELETE | `/{id}` | delete | path 附件 `id`，query：`entid` | ✅ |
| 3 | PUT | `/real_name/{id}` | realName | path `id`，query：`entid`，body：`real_name` | ✅ |

---

## 四、需新增/补充的实体

| 表名 | 实体类（Java） | 说明 | 当前状态 |
|------|----------------|------|---------|
| eb_program | Program | | ✅ |
| eb_program_task | ProgramTask | 全字段 + 列表用 transient 条件 | ✅ |
| eb_program_version | ProgramVersion | | ✅ |
| eb_program_dynamic | ProgramDynamic | | ✅ |
| eb_program_task_comment | ProgramTaskComment | 表名非 `eb_task_comment` | ✅ |
| eb_program_task_member | ProgramTaskMember | 任务协作者 | ✅ |
| eb_program_member | ProgramMember | | ✅ |
| eb_system_attach | SystemAttach | 项目附件：`relation_type = 9`，`relation_id = program_id`；**无** `eb_program_file` 表 | ✅（复用） |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 / 模块 | 优先级 | 状态 |
|------|------|----------------|--------|------|
| 1 | 任务管理全量接口（与 PHP 路径一致） | ProgramTaskController | P0 | ✅ |
| 2 | 版本管理 3 接口 | ProgramVersionController | P0 | ✅ |
| 3 | 项目动态 2 接口 | ProgramDynamicController | P1 | ✅ |
| 4 | 任务评论 4 接口 | ProgramTaskCommentController | P1 | ✅ |
| 5 | 项目文件 3 接口（Attach） | ProgramFileController | P1 | ✅ |
| 6 | 任务写操作写入 `eb_program_dynamic` | ProgramTaskOaServiceImpl + ProgramDynamicOaService | P1 | ✅（核心路径已接入） |
| 7 | 实体 ProgramVersion / ProgramDynamic / ProgramTaskComment / ProgramTaskMember | bubble-api-oa | P0 | ✅ |

**文档约定**: 接口表以 **PHP**（`code/app/Http/Controller/AdminApi/Program/*.php`）与 **Java**（`bubble-biz-oa` 对应 `*Controller`）为准；若与早期草案冲突，以双方实现一致为准。
