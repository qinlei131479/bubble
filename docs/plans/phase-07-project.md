# 阶段七：项目管理 (P2)

> **优先级**: P2  
> **前置依赖**: 阶段一（用户/组织）  
> **预估工时**: 2 周  
> **主计划**: [oa-migration-plan.md](./oa-migration-plan.md)

---

## 一、目标与验收标准

前端项目管理模块完整可用：**项目 CRUD → 任务管理（含子任务/批量/排序/分享）→ 版本管理 → 动态 → 评论 → 附件**

### 验收检查清单

- [ ] 项目 CRUD + 下拉列表 + 成员管理 + 详情
- [ ] 任务 CRUD + 子任务 + 批量更新/删除 + 排序 + 分享
- [ ] 版本管理列表/创建/下拉
- [ ] 项目动态/任务动态查看
- [ ] 任务评论 CRUD
- [ ] 项目文件列表/删除/重命名
- [ ] 所有接口与 PHP 同路径、同参数、同响应结构

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

**PHP Prefix**: `ent/program` | **Java**: `@RequestMapping("/ent/program")`  
**状态**: 已有，大部分已实现

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 项目列表 | ✅ |
| 2 | POST | / | store | 创建项目 | ✅ |
| 3 | PUT | /{id} | update | 修改项目 | ✅ |
| 4 | DELETE | /{id} | destroy | 删除项目 | ✅ |
| 5 | GET | /select | select | 下拉列表 | ✅ |
| 6 | GET | /members/{id} | members | 项目成员 | ✅ |
| 7 | GET | /{id} | detail | 项目详情 | ✅ |

### 3.2 ProgramTaskController — 任务管理

**PHP Prefix**: `ent/program_task` | **Java**: `@RequestMapping("/ent/program_task")`  
**状态**: 需新增或补全

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 任务列表 | ⚠️ 需实现：分页 + 按项目/版本/状态/负责人筛选 |
| 2 | POST | / | store | 创建任务 | ⚠️ 需实现：写 `eb_program_task` + 关联项目 |
| 3 | PUT | /{id} | update | 修改任务 | ⚠️ 需实现 |
| 4 | DELETE | /{id} | destroy | 删除任务 | ⚠️ 需实现：联删子任务 |
| 5 | POST | /sub/{pid} | storeSub | 保存子任务 | ⚠️ 需实现：`pid` 为父任务 ID |
| 6 | GET | /{id} | detail | 任务详情 | ⚠️ 需实现：含子任务列表 |
| 7 | GET | /select | select | 下拉列表 | ⚠️ 需实现 |
| 8 | PUT | /batch | batchUpdate | 批量更新（状态/负责人等） | ⚠️ 需实现 |
| 9 | DELETE | /batch | batchDestroy | 批量删除 | ⚠️ 需实现 |
| 10 | PUT | /sort | sort | 排序 | ⚠️ 需实现：接收 ID 数组更新 `sort` 字段 |
| 11 | POST | /share/{id} | share | 分享任务 | ⚠️ 需实现 |

**实现要点**:
- 任务含层级关系：`pid` 字段表示父任务，子任务通过 `pid` 关联
- 批量操作需事务保证
- 排序接收有序 ID 数组，按序更新 `sort` 字段

### 3.3 ProgramVersionController — 版本管理

**PHP Prefix**: `ent/program_version` | **Java**: `@RequestMapping("/ent/program_version")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 版本列表 | ❌ 需新增 |
| 2 | POST | / | store | 保存版本 | ❌ 需新增 |
| 3 | GET | /select | select | 下拉列表 | ❌ 需新增 |

### 3.4 ProgramDynamicController — 项目动态

**PHP Prefix**: `ent/program_dynamic` | **Java**: `@RequestMapping("/ent/program_dynamic")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | /program/{program_id} | programDynamic | 项目动态 | ❌ 需新增：查 `eb_program_dynamic` 按项目筛选 |
| 2 | GET | /task/{task_id} | taskDynamic | 任务动态 | ❌ 需新增：查动态按任务筛选 |

**实现要点**: 动态记录在任务/项目 CRUD 时自动写入 `eb_program_dynamic`，控制器仅查询展示

### 3.5 ProgramTaskCommentController — 任务评论

**PHP Prefix**: `ent/task_comment` | **Java**: `@RequestMapping("/ent/task_comment")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 评论列表 | ❌ 需新增 |
| 2 | POST | / | store | 保存评论 | ❌ 需新增 |
| 3 | PUT | /{id} | update | 修改评论 | ❌ 需新增 |
| 4 | DELETE | /{id} | destroy | 删除评论 | ❌ 需新增 |

### 3.6 ProgramFileController — 项目文件

**PHP Prefix**: `ent/program_file` | **Java**: `@RequestMapping("/ent/program_file")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 | 状态 |
|---|------|------|----------|------|------|
| 1 | GET | / | index | 文件列表 | ❌ 需新增 |
| 2 | DELETE | /{id} | destroy | 删除文件 | ❌ 需新增 |
| 3 | PUT | /rename/{id} | rename | 重命名 | ❌ 需新增 |

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_program | Program | ✅ 已有 |
| eb_program_task | ProgramTask | ✅ 已有 |
| eb_program_version | ProgramVersion | 需新增 |
| eb_program_dynamic | ProgramDynamic | 需新增 |
| eb_task_comment | TaskComment | 需新增 |
| eb_program_file | ProgramFile | 需新增 |
| eb_program_member | ProgramMember | ✅ 已有 |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 实现任务管理全量接口 (11接口) | ProgramTaskController | P0 |
| 2 | 新增版本管理控制器 (3接口) | ProgramVersionController | P0 |
| 3 | 新增项目动态控制器 (2接口) | ProgramDynamicController | P1 |
| 4 | 新增任务评论控制器 (4接口) | ProgramTaskCommentController | P1 |
| 5 | 新增项目文件控制器 (3接口) | ProgramFileController | P1 |
| 6 | 任务 CRUD 时自动写入动态记录 | ProgramTaskServiceImpl | P1 |
| 7 | 新增实体：ProgramVersion/ProgramDynamic/TaskComment/ProgramFile | bubble-api-oa | P0 |
