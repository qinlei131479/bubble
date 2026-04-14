# 阶段十：开放 API (P2)

> **优先级**: P2（早于阶段八实施**主体**，与阶段九同档）  
> **前置依赖**: 阶段一、二；阶段五（CRM 客户/合同/发票等）；阶段四（日程 — `OpenScheduleController`）。**低代码数据开放**（`OpenModuleController`）依赖阶段八，须与阶段八**分步交付**：其余开放接口不等待低代码。  
> **预估工时**: 1.5-2 周（若含低代码开放收尾，与阶段八衔接后再计半周～1 周）  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

第三方可通过开放 API 操作客户、合同、发票、联系人、日程、低代码数据；内部 API Key 管理可用。

### 验收检查清单

- [ ] API Key 管理：列表/创建/编辑/删除/角色/文档/查找 SK
- [ ] 开放授权登录：通过 API Key + Secret 获取 Token
- [ ] 客户管理 Open API（创建/修改/删除）
- [ ] 合同管理 Open API（创建/修改/删除）
- [ ] 发票管理 Open API（保存/作废）
- [ ] 联系人管理 Open API（创建/修改/删除）
- [ ] 日程管理 Open API（创建/修改/删除）
- [ ] 付款/续费 Open API
- [ ] 低代码数据 Open API（列表/创建/修改/获取/删除）— **阶段八完成后**再与开放 API 全量验收
- [ ] `OpenApiWave7StubController` 占位桩全部替换为真实逻辑
- [ ] 开放 API 鉴权中间件正常工作

---

## 二、模块执行顺序

```
1. OpenApiKeyController    — Key 管理是开放 API 鉴权基础
2. OpenAuthController       — 授权登录（依赖 Key）
3. OpenCustomerController   — 客户（依赖阶段五 CRM）
4. OpenContractController   — 合同
5. OpenInvoiceController    — 发票
6. OpenLiaisonController    — 联系人
7. OpenBillController       — 付款/续费
8. OpenScheduleController   — 日程（依赖阶段四）
9. OpenModuleController     — 低代码数据（**仅此项**依赖阶段八，宜在阶段八后实施）
10. OpenDocController       — 文档端点（仅中间件挂载）
```

> **关键**: 必须先实现 Key 管理 + 授权登录，构建开放 API 鉴权体系，再逐一开放业务接口。  
> **分步**: 步骤 1～8、10 可在阶段八前完成；步骤 9 与阶段八衔接。

---

## 三、接口清单

### 3.1 OpenApiKeyController — API Key 管理

**PHP Prefix**: `ent/openapi` | **Java**: `@RequestMapping("/ent/openapi")`  
**状态**: 需全量新增（替换 `OpenApiWave7StubController` 部分路径）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /key/list | keyList | Key 列表 |
| 2 | POST | /key | keyStore | 创建 Key |
| 3 | GET | /key/{id}/edit | keyEdit | 编辑表单 |
| 4 | PUT | /key/{id} | keyUpdate | 修改 Key |
| 5 | DELETE | /key/{id} | keyDestroy | 删除 Key |
| 6 | GET | /key/{id} | keyShow | Key 详情 |
| 7 | GET | /roles | roles | 可分配角色列表 |
| 8 | GET | /doc | doc | API 文档端点 |
| 9 | GET | /find_sk/{ak} | findSk | 通过 AK 查找 SK |

**实现要点**:
- Key 包含 `access_key`(AK) 和 `secret_key`(SK)，AK 公开、SK 加密存储
- 创建时自动生成 UUID 格式的 AK/SK
- Key 可关联角色，控制开放 API 访问范围
- `findSk`: 仅管理员可用，用于调试

### 3.2 OpenAuthController — 开放授权登录

**PHP Prefix**: `open/auth` | **Java**: `@RequestMapping("/open/auth")`  
**状态**: 需新增（当前为占位）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | /login | login | 接口授权登录（AK+SK → Token） |

**实现要点**:
- 接收 `access_key` + `secret_key` + 签名参数
- 验证 AK 有效 → 校验 SK → 生成临时 Token（有效期可配置）
- Token 用于后续开放 API 请求的 Bearer 鉴权
- PHP 使用独立中间件校验，Java 侧可通过 Spring Security filter 或自定义拦截器

### 3.3 OpenCustomerController — 客户管理

**PHP Prefix**: `open/customer` | **Java**: `@RequestMapping("/open/customer")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | / | store | 创建客户 |
| 2 | PUT | /{id} | update | 修改客户 |
| 3 | DELETE | /{id} | destroy | 删除客户 |

**实现要点**: 复用 `CrmCustomerServiceImpl` 的核心逻辑，加开放 API 鉴权拦截

### 3.4 OpenContractController — 合同管理

**PHP Prefix**: `open/contract` | **Java**: `@RequestMapping("/open/contract")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | / | store | 创建合同 |
| 2 | PUT | /{id} | update | 修改合同 |
| 3 | DELETE | /{id} | destroy | 删除合同 |

### 3.5 OpenInvoiceController — 发票管理

**PHP Prefix**: `open/invoice` | **Java**: `@RequestMapping("/open/invoice")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | / | store | 保存发票 |
| 2 | POST | /void/{id} | void | 作废发票 |

### 3.6 OpenLiaisonController — 联系人管理

**PHP Prefix**: `open/liaison` | **Java**: `@RequestMapping("/open/liaison")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | / | store | 创建联系人 |
| 2 | PUT | /{id} | update | 修改联系人 |
| 3 | DELETE | /{id} | destroy | 删除联系人 |

### 3.7 OpenBillController — 付款/续费

**PHP Prefix**: `open/bill` | **Java**: `@RequestMapping("/open/bill")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | /pay | pay | 付款 |
| 2 | POST | /renew | renew | 续费 |
| 3 | POST | /expense | expense | 支出 |
| 4 | DELETE | /{id} | destroy | 删除 |
| 5 | POST | /remind | remind | 提醒 |

### 3.8 OpenScheduleController — 日程管理

**PHP Prefix**: `open/schedule` | **Java**: `@RequestMapping("/open/schedule")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | POST | / | store | 创建日程 |
| 2 | PUT | /{id} | update | 修改日程 |
| 3 | DELETE | /{id} | destroy | 删除日程 |

### 3.9 OpenModuleController — 低代码数据

**分步交付**: 须待阶段八 `ModuleController` 等动态 CRUD 能力就绪后再实现本节；阶段 10 其余控制器无此依赖。

**PHP Prefix**: `open/module` | **Java**: `@RequestMapping("/open/module")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /{name} | index | 列表 |
| 2 | GET | /{name}/create | createForm | 创建表单 |
| 3 | POST | /{name} | store | 保存数据 |
| 4 | PUT | /{name}/{id} | update | 修改数据 |
| 5 | GET | /{name}/{id} | show | 获取数据 |
| 6 | DELETE | /{name}/{id} | destroy | 删除数据 |

**实现要点**: 复用阶段八 `ModuleController` 的动态数据操作逻辑，加开放 API 鉴权和权限校验

### 3.10 OpenDocController — API 文档

**PHP Prefix**: `ent/openapi` | **状态**: 仅中间件挂载

> PHP 中此控制器为空，仅用于中间件挂载点。Java 侧可通过 SpringDoc 自动生成开放 API 文档。

---

## 四、开放 API 鉴权架构

```
第三方请求 → Gateway → OpenAPI 鉴权 Filter
                              ↓
                      校验 Bearer Token
                              ↓
                      读取 Key 关联角色/权限
                              ↓
                      注入安全上下文
                              ↓
                      业务 Controller
```

**实现方案**:
1. 在 `bubble-biz-oa` 新增 `OpenApiSecurityFilter`（或 Spring Security 配置）
2. 拦截 `/open/**` 路径请求
3. 解析 Bearer Token → 查 Redis/DB 获取 Key 信息 → 校验有效期和权限
4. 与 OA 内部接口的 PHP JWT 鉴权隔离

---

## 五、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_open_api_key | OpenApiKey | 需新增 |

---

## 六、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 新增 OpenApiKey 实体 | bubble-api-oa | P0 |
| 2 | 实现开放 API 鉴权 Filter | SecurityConfig | P0 |
| 3 | 实现 Key 管理 (9接口) | OpenApiKeyController | P0 |
| 4 | 实现授权登录 | OpenAuthController | P0 |
| 5 | 实现客户 Open API (3接口) | OpenCustomerController | P0 |
| 6 | 实现合同 Open API (3接口) | OpenContractController | P0 |
| 7 | 实现发票 Open API (2接口) | OpenInvoiceController | P1 |
| 8 | 实现联系人 Open API (3接口) | OpenLiaisonController | P1 |
| 9 | 实现付款 Open API (5接口) | OpenBillController | P1 |
| 10 | 实现日程 Open API (3接口) | OpenScheduleController | P1 |
| 11 | 实现低代码 Open API (6接口)，**阶段八后** | OpenModuleController | P1 |
| 12 | 替换 OpenApiWave7StubController 全部占位 | — | P0 |
| 13 | 配置 SpringDoc 生成开放 API 文档 | — | P2 |
