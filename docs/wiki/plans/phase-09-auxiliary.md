# 阶段九：辅助功能模块 (P2)

> **优先级**: P2（早于阶段八实施，不等待低代码）  
> **前置依赖**: 阶段一（用户/组织）、阶段二（权限/附件）  
> **预估工时**: 2-3 周  
> **主计划**: [oa-migration-plan.md](oa-migration-plan.md)

---

## 一、目标与验收标准

前端云盘、通知公告、物资管理、帮助中心、Chat 对话模块完整可用。

### 验收检查清单

- [ ] 云盘：空间管理 + 文件管理（含分片上传/移动/复制/权限/回收站）
- [ ] 通知公告：新闻 CRUD + 分类管理 + 置顶
- [ ] 企业消息：列表/处理状态/批量操作/订阅
- [ ] 物资管理：分类 + 物资 CRUD + 领用/维修记录
- [ ] 帮助中心：聚合搜索
- [ ] Chat/AI 对话：应用管理 + 模型管理 + 历史会话 + 对话记录（**在 OA 内实现**；与 AGI 无当前依赖，后续再统一归并）
- [ ] `CloudFileWave7Controller`、`CloudSpaceWave7Controller`、`UserCenterController` 占位桩清零

---

## 二、模块执行顺序

```
1. CloudSpaceController          — 空间是文件的容器
2. CloudFileController           — 文件管理（依赖空间）
3. NewsCateController            — 新闻分类（被新闻引用）
4. NewsController                — 新闻/公告
5. NoticeRecordController        — 企业消息
6. StorageCategoryController     — 物资分类
7. StorageController             — 物资管理
8. StorageRecordController       — 领用/维修记录
9. HelpCenterController          — 帮助中心（独立）
10. UserCenterController         — 用户中心简历（消除占位）
11. ChatApplicationsController   — Chat 应用（OA 内落地，后续与 AGI 统一时再归并）
12. ChatModelsController         — Chat 模型
13. ChatHistoryController        — Chat 历史
14. ChatRecordController         — Chat 记录
```

---

## 三、接口清单

### 3.1 CloudSpaceController — 云盘空间

**PHP Prefix**: `ent/cloud/space` | **Java**: `@RequestMapping("/ent/cloud/space")`  
**状态**: 需全量新增（替换 `CloudSpaceWave7Controller` 占位）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 空间列表 |
| 2 | GET | /dir | dir | 目录结构 |
| 3 | GET | /recent | recent | 最近文件 |
| 4 | GET | /trash | trash | 回收站 |
| 5 | POST | / | store | 创建空间 |
| 6 | GET | /permission/{id} | permission | 权限详情 |
| 7 | PUT | /{id} | update | 修改空间 |
| 8 | DELETE | /{id} | destroy | 删除（移入回收站） |
| 9 | DELETE | /force/{id} | forceDestroy | 彻底删除 |
| 10 | DELETE | /force/batch | batchForceDestroy | 批量彻底删除 |
| 11 | PUT | /restore/{id} | restore | 恢复 |
| 12 | PUT | /restore/batch | batchRestore | 批量恢复 |
| 13 | PUT | /transfer/{id} | transfer | 转让空间 |

### 3.2 CloudFileController — 云盘文件

**PHP Prefix**: `ent/cloud/file/{fid}` | **Java**: `@RequestMapping("/ent/cloud/file/{fid}")`  
**状态**: 需全量新增（替换 `CloudFileWave7Controller` 占位）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 文件列表 |
| 2 | POST | / | store | 创建文件 |
| 3 | POST | /folder | folder | 创建文件夹 |
| 4 | PUT | /{id} | update | 更新文件 |
| 5 | DELETE | /{id} | destroy | 删除文件 |
| 6 | DELETE | /batch | batchDestroy | 批量删除 |
| 7 | GET | /{id} | detail | 文件详情 |
| 8 | POST | /upload | upload | 上传文件 |
| 9 | POST | /save | save | 保存文件内容 |
| 10 | PUT | /move/{id} | move | 移动文件 |
| 11 | PUT | /move/batch | batchMove | 批量移动 |
| 12 | POST | /copy/{id} | copy | 复制文件 |
| 13 | PUT | /rename/{id} | rename | 重命名 |
| 14 | GET | /permission/{id} | permission | 权限详情 |
| 15 | PUT | /permission/{id} | updatePermission | 修改权限 |
| 16 | GET | /template | template | 模板下载 |
| 17 | POST | /chunk_upload | chunkUpload | 分片上传 |

### 3.3 NewsCateController — 新闻分类

**PHP Prefix**: `ent/notice/category` | **Java**: `@RequestMapping("/ent/notice")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /category | cateIndex | 分类列表 |
| 2 | GET | /category/create | cateCreate | 创建表单 |
| 3 | POST | /category | cateStore | 保存分类 |
| 4 | GET | /category/{id}/edit | cateEdit | 编辑表单 |
| 5 | PUT | /category/{id} | cateUpdate | 修改分类 |
| 6 | DELETE | /category/{id} | cateDestroy | 删除分类 |
| 7 | GET | /category/{id} | cateShow | 分类详情 |

### 3.4 NewsController — 新闻/通知公告

**PHP Prefix**: `ent/notice/list` | **Java**: `@RequestMapping("/ent/notice")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /list | index | 公告列表 |
| 2 | GET | /list/create | create | 创建表单 |
| 3 | POST | /list | store | 保存公告 |
| 4 | GET | /list/{id}/edit | edit | 编辑表单 |
| 5 | PUT | /list/{id} | update | 修改公告 |
| 6 | DELETE | /list/{id} | destroy | 删除公告 |
| 7 | GET | /list/{id} | detail | 公告详情 |
| 8 | GET | /list/all | all | 全部选项 |
| 9 | PUT | /list/top/{id} | top | 置顶/取消置顶 |

### 3.5 NoticeRecordController — 企业消息

**PHP Prefix**: `ent/company/message` | **Java**: `@RequestMapping("/ent/company/message")`  
**状态**: 需新增（`NoticeBoardController` 部分存在但占位）

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 消息列表 |
| 2 | GET | /all | all | 全部消息列表 |
| 3 | PUT | /handle/{id} | handle | 处理状态 |
| 4 | PUT | /batch_read | batchRead | 批量已读 |
| 5 | DELETE | /batch | batchDestroy | 批量删除 |
| 6 | GET | /subscribe | subscribe | 订阅列表 |
| 7 | PUT | /subscribe/{id} | toggleSubscribe | 订阅切换 |

### 3.6 StorageCategoryController — 物资分类

**PHP Prefix**: `ent/storage/cate` | **Java**: `@RequestMapping("/ent/storage")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /cate | cateIndex | 分类列表 |
| 2 | POST | /cate | cateStore | 保存分类 |
| 3 | GET | /cate/{id}/edit | cateEdit | 编辑表单 |
| 4 | PUT | /cate/{id} | cateUpdate | 修改分类 |
| 5 | DELETE | /cate/{id} | cateDestroy | 删除分类 |
| 6 | GET | /cate/create | cateCreate | 创建表单 |

### 3.7 StorageController — 物资管理

**PHP Prefix**: `ent/storage/list` | **Java**: `@RequestMapping("/ent/storage")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /list | index | 物资列表 |
| 2 | POST | /list | store | 保存物资 |
| 3 | PUT | /list/{id} | update | 修改物资 |
| 4 | POST | /list/cate | moveCate | 修改分类 |

### 3.8 StorageRecordController — 领用/维修记录

**PHP Prefix**: `ent/storage/record` | **Java**: `@RequestMapping("/ent/storage/record")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 记录列表 |
| 2 | POST | / | store | 保存记录 |
| 3 | PUT | /{id} | update | 修改记录 |
| 4 | GET | /users/{id} | users | 关联人员 |
| 5 | GET | /history/{id} | history | 历史人员 |
| 6 | POST | /census | census | 统计 |
| 7 | GET | /repair/{id} | repair | 维修详情 |

### 3.9 HelpCenterController — 帮助中心

**PHP Prefix**: `ent/helps` | **Java**: `@RequestMapping("/ent/helps")`  
**状态**: 需新增

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /aggregate | aggregate | 聚合搜索 |

### 3.10 UserCenterController — 用户中心

**PHP Prefix**: `ent/user/center` | **Java**: `@RequestMapping("/ent/user/center")`  
**状态**: 占位，需实现

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /resume | resume | 简历分页 |

**实现要点**: 消除 `SimplePageVO.empty` 占位，查 `eb_user_resume` 分页返回

### 3.11 Chat 模块（OA 内实现，与 AGI 解耦）

> **定位**: Chat/AI 对话（应用、模型、历史会话、对话记录）**当前与 AGI 模块无功能边界争议**：不在本阶段等待 AGI 对齐或复用 AGI 接口。  
> **实施**: 在 `bubble-biz-oa`（及 `bubble-api-oa` 实体/DTO）按 PHP 路由与 `eb_chat_*` 表**完整实现**业务与接口。  
> **后续**: 平台若将对话能力统一收敛到 AGI，再做单次迁移/归并（接口与数据模型届时另行方案），本计划不阻塞 OA 交付。

#### 3.11.1 ChatApplicationsController (8 接口)

**PHP Prefix**: `ent/chat/applications`

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 应用列表 |
| 2 | GET | /create | create | 创建表单 |
| 3 | POST | / | store | 保存应用 |
| 4 | GET | /{id}/edit | edit | 编辑表单 |
| 5 | PUT | /{id} | update | 修改应用 |
| 6 | DELETE | /{id} | destroy | 删除应用 |
| 7 | PUT | /status/{id} | status | 状态切换 |
| 8 | GET | /select | select | 下拉列表 |

#### 3.11.2 ChatModelsController (8 接口)

**PHP Prefix**: `ent/chat/models`

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 模型列表 |
| 2 | POST | / | store | 保存模型 |
| 3 | GET | /{id}/edit | edit | 编辑表单 |
| 4 | PUT | /{id} | update | 修改模型 |
| 5 | DELETE | /{id} | destroy | 删除模型 |
| 6 | GET | /select | select | 下拉列表 |
| 7 | GET | /provider | provider | 供应商列表 |
| 8 | GET | /types | types | 模型类型 |

#### 3.11.3 ChatHistoryController (10 接口)

**PHP Prefix**: `ent/chat/history`

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | / | index | 历史会话列表 |
| 2 | GET | /apps | apps | 应用列表 |
| 3 | GET | /{id} | detail | 会话详情 |
| 4 | POST | / | store | 创建会话 |
| 5 | PUT | /{id} | update | 更新会话 |
| 6 | DELETE | /{id} | destroy | 删除会话 |
| 7 | POST | /chat/{id} | chat | 发送对话 |
| 8 | POST | /stop/{id} | stop | 中断对话 |
| 9 | DELETE | /clear/{id} | clear | 清理记录 |
| 10 | GET | /records/{id} | records | 对话记录 |

#### 3.11.4 ChatRecordController (1 接口)

**PHP Prefix**: `ent/chat/record`

| # | HTTP | Path | Java 方法 | 说明 |
|---|------|------|----------|------|
| 1 | GET | /list | list | 聊天记录列表 |

---

## 四、需新增/补充的实体

| 表名 | 实体类 | 当前状态 |
|------|--------|---------|
| eb_cloud_file | CloudFile | 需新增 |
| eb_cloud_space | CloudSpace | 需新增 |
| eb_news | News | 需新增 |
| eb_news_cate | NewsCategory | 需新增 |
| eb_notice_record | NoticeRecord | 需核验 |
| eb_storage_cate | StorageCategory | 需新增 |
| eb_storage | Storage | 需新增 |
| eb_storage_record | StorageRecord | 需新增 |
| eb_chat_applications | ChatApplication | 需新增（OA 侧实体，后续归并 AGI 时再评估） |
| eb_chat_models | ChatModel | 需新增（同上） |
| eb_chat_history | ChatHistory | 需新增（同上） |
| eb_chat_record | ChatRecord | 需新增（同上） |

---

## 五、本阶段待办汇总

| 序号 | 任务 | 控制器 | 优先级 |
|------|------|--------|--------|
| 1 | 替换云盘空间占位，实现全量 (13接口) | CloudSpaceController | P0 |
| 2 | 替换云盘文件占位，实现全量 (17接口) | CloudFileController | P0 |
| 3 | 新增新闻分类 (7接口) | NewsCateController | P1 |
| 4 | 新增通知公告 (9接口) | NewsController | P1 |
| 5 | 新增企业消息 (7接口) | NoticeRecordController | P1 |
| 6 | 新增物资分类 (6接口) | StorageCategoryController | P1 |
| 7 | 新增物资管理 (4接口) | StorageController | P1 |
| 8 | 新增领用/维修记录 (7接口) | StorageRecordController | P1 |
| 9 | 新增帮助中心 (1接口) | HelpCenterController | P2 |
| 10 | 消除用户中心简历占位 | UserCenterController | P1 |
| 11 | Chat 全量在 OA 实现 (27接口)，不依赖 AGI | Chat*Controller | P2 |
