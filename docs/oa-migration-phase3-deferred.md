# OA 迁移 Phase 3（企业与人事）— 未完项记录

> **用途**：本阶段有部分能力未对齐 PHP 全量接口；待 **整体 migration plan 各阶段收尾后**，再按本文逐项细化实现与联调。  
> **关联计划**：`~/.cursor/plans/php-to-java_oa_migration_6dd94852.plan.md`（Phase 3：企业与人事管理，约 66 接口）

---

## 已实现（可作基线）

| 区域 | 说明 |
|------|------|
| 企业管理 | `GET/PUT /ent/company/info`，`GET /ent/company/quantity/{type}`；`Enterprise` 扩展字段；邀请审核计数 `eb_user_enterprise_apply` |
| 企业用户 | `GET /ent/user/list`、`/card/{id}`、`/userInfo`、`/userFrame`、`/add_book/tree`、`/add_book/list`（部分与 PHP 行为简化对齐） |
| 晋升主表 | `ent/company/promotions` CRUD（`eb_promotion`） |
| 员工培训 | `GET/PUT /ent/company/train/{type}`（三种类型，`eb_employee_train`） |
| 调薪记录 | `ent/company/salary` 列表/编辑/增删改、`GET .../last/{card_id}`（`eb_enterprise_user_salary`） |
| 占位保留 | `CompanyController` 中 `/user-card/page`、`/message` 仍为占位 |

---

## 待完成 / 需细化（Phase 3 范围内）

### 1. 海氏评估 `ent/company/evaluate`（HayGroup）

- **表**：`eb_hay_group`、`eb_hay_group_data`
- **PHP**：`HayGroupController` + `HayGroupService`（保存/修改时组头 + 多行子表事务；`uuid_to_uid` 与 `uid` 权限校验）
- **缺口**：Java 侧未实现列表、保存、修改、删除、`GET data/{group_id}`、`GET history/{group_id}` 等与 PHP 一致的行为；**需先确认 `hay_group.uid` 与 Java 当前用户标识（admin.id / uid）映射规则**。

### 2. 企业用户 `ent/user`（CompanyUserController 剩余接口）

- **PHP**：`list`（与 `index` 可能并存）、`info/{id}`、`update/{id}`、`addMoreMember`、`edit/{id}`、`saveUserRole/{id}`、`PUT card/{id}`（`saveAdminFrame`）、`getApply`、`destroyApply`、`getApply` 状态筛选等
- **缺口**：组织架构成员增改、批量导入、邀请列表/删除、角色保存等；依赖 `CompanyUserService` / `AdminService` 与 PHP `FrameAssist`、`EnterpriseRole` 等一致事务

### 3. 员工档案 / 工卡 `ent/company/card`（UserCardController）

- **表**：`eb_admin` + `eb_admin_info` 及关联经历表等
- **PHP**：`UserCardController`（列表 POST、导入、树、保存多场景、导入、详情、修改、子资源等，约 19 接口量级）
- **缺口**：整控制器未迁移；与 Phase 3 计划中的「员工档案」强相关

### 4. 晋升子数据 `ent/company/promotion/data`（PromotionDataController）

- **表**：`eb_promotion_data`
- **PHP**：资源路由 + `POST standard/{id}`、`POST sort/{pid}`
- **缺口**：仅实现主表 `promotion`，子表与排序/标准修改未做

### 5. Company 小模块（PHP `Company/*` 其余控制器）

- **示例**：`CompanyEducationController`、`CompanyWorkController`、`CompanyPositionController`、`CompanyPerfectController` 等（教育/工作经历/岗位/档案完善等）
- **缺口**：按表 `eb_enterprise_user_education`、`eb_enterprise_user_work`、`eb_enterprise_user_position`、`eb_user_card_perfect` 等分别建模与接口对齐

### 6. 调薪列表与 PHP 低代码差异

- **PHP**：`UserSalaryService::getSalaryList` 部分逻辑走 **低代码 Crud 模块**（`gongzitiaojilu`），与直接查 `eb_enterprise_user_salary` 可能不一致
- **缺口**：若前端依赖 Crud 动态字段/展示，需在 Java 侧约定：**仅表查询** 还是 **复刻 Crud 聚合结构**（建议在收尾阶段与前端对一下响应形状）

### 7. 响应与分页形态

- 全量联调时统一：**PhpResponse** 字段、`SimplePageVO` 与 PHP Laravel 分页字段（`current_page` / `per_page` / `total` / `data`）是否需 **ResponseBodyAdvice** 完全对齐（跨 Phase 事项，收尾阶段处理即可）

---

## 建议执行顺序（仅 Phase 3 收尾时）

1. **UserCard** 主链路（列表、详情、编辑）→ 再子资源（教育/工作/岗位等）  
2. **CompanyUser** 剩余接口（与 Frame/角色一致）  
3. **HayGroup** 完整事务 + 子表  
4. **PromotionData** + 小控制器  
5. 最后做 **Salary 与 Crud 一致性** 与全局分页/响应适配  

---

## 变更记录

| 日期 | 说明 |
|------|------|
| 2026-03-29 | 初始记录：Phase 3 未完项，待整 plan 完成后细化 |
