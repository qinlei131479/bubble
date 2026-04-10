<template>
  <el-drawer
      v-model="visible"
      :title="`${datasourceName} - 表结构`"
      :close-on-click-modal="false"
      size="80%"
      destroy-on-close
  >
    <div class="table-detail-container">
      <!-- 左侧：表列表 -->
      <div class="table-list-panel">
        <div class="panel-header">
          <span class="panel-title">表结构列表</span>
          <span class="table-count">共 {{ tableList.length }} 张表</span>
        </div>
        <div class="panel-search">
          <el-input v-model="tableSearch" placeholder="请输入表名或表注释搜索" clearable>
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </div>
        <div class="panel-content" v-loading="tableLoading">
          <el-empty v-if="filteredTableList.length === 0 && !tableLoading" description="暂无表数据"/>
          <div v-else class="table-item-list">
            <div
                v-for="table in filteredTableList"
                :key="table.id"
                :class="['table-item', { active: currentTable?.id === table.id }]"
                @click="handleSelectTable(table)"
            >
              <div class="table-item-name">{{ table.tableName }}</div>
              <div class="table-item-comment">{{ table.tableComment || '无注释' }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：字段列表 -->
      <div class="field-list-panel">
        <div class="panel-header" style="flex-shrink: 0;">
          <div class="panel-title-group">
            <span class="panel-title">表字段信息</span>
            <span v-if="currentTable" class="table-count">
              {{ currentTable.tableName }} - {{ fieldList.length }} 个字段
            </span>
          </div>
          <div v-if="currentTable" class="panel-actions">
            <el-button type="primary" size="small" :loading="saveLoading" @click="handleSaveFields">
              批量更新
            </el-button>
          </div>
        </div>

        <div v-if="currentTable" class="table-base-info">
          <div class="table-base-row">
            <span class="label">表名：</span>
            <span class="value">{{ currentTable.tableName }}</span>
          </div>
          <div class="table-base-row">
            <span class="label">备注：</span>
            <span
                v-if="!editingTableComment"
                class="value editable"
                @click="startEditTableComment"
            >
              {{ tableCommentInput || '点击编辑备注' }}
            </span>
            <el-input
                v-else
                v-model="tableCommentInput"
                class="table-comment-input"
                placeholder="请输入表备注"
                @blur="saveTableComment"
            />
          </div>
        </div>

        <div class="panel-content" style="overflow-x: auto;" v-loading="fieldLoading">
          <el-empty v-if="!currentTable" description="请先选择左侧表"/>
          <el-empty v-else-if="fieldList.length === 0 && !fieldLoading" description="暂无字段数据"/>
          <el-table v-else :data="fieldList" border height="100%" style="width: 100%">
            <el-table-column type="index" label="#" width="50" align="center"/>
            <el-table-column prop="fieldName" label="字段名" min-width="150" show-overflow-tooltip/>
            <el-table-column prop="fieldType" label="字段类型" width="120"/>
            <el-table-column prop="fieldComment" label="字段注释" min-width="150" show-overflow-tooltip/>
            <el-table-column prop="customComment" label="字段注释(自定义)" min-width="200">
              <template #default="scope">
                <el-input
                    v-model="scope.row.customComment"
                    size="default"
                    placeholder="请输入自定义注释"
                />
              </template>
            </el-table-column>
            <el-table-column prop="dataMapping" label="数据映射" min-width="220">
              <template #default="scope">
                <el-input
                    v-model="scope.row.dataMapping"
                    size="default"
                    placeholder="请输入数据映射规则，例如：1=男,2=女，格式：原始值=映射值，多个用逗号分隔"
                />
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="排序" width="100" align="center"/>
          </el-table>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup lang="ts" name="DatasourceTableDetail">
import {getTableObj, putTableObj, getFieldObj, putFieldObj} from '/@/api/agi/datasource'
import {Search} from '@element-plus/icons-vue';
import {useMessage} from "/@/hooks/message";

const emit = defineEmits(['refresh']);

// 弹窗相关
const visible = ref(false)
const datasourceId = ref('')
const datasourceName = ref('')

// 表格相关
const tableLoading = ref(false)
const tableList = ref<any[]>([])
const tableSearch = ref('')
const currentTable = ref<any>(null)

// 字段相关
const fieldLoading = ref(false)
const fieldList = ref<any[]>([])
const saveLoading = ref(false)

// 表备注编辑
const editingTableComment = ref(false)
const tableCommentInput = ref('')

// 过滤后的表列表
const filteredTableList = computed(() => {
  if (!tableSearch.value) {
    return tableList.value;
  }
  const search = tableSearch.value.toLowerCase();
  return tableList.value.filter(table =>
      table.tableName.toLowerCase().includes(search) ||
      (table.tableComment && table.tableComment.toLowerCase().includes(search))
  );
});

// 打开弹窗
const openDialog = async (row: any) => {
  visible.value = true;
  datasourceId.value = row.id;
  datasourceName.value = row.name;

  // 重置状态
  tableSearch.value = '';
  currentTable.value = null;
  fieldList.value = [];

  // 加载表列表
  await loadTableList();
};

// 加载表列表
const loadTableList = async () => {
  tableLoading.value = true;
  try {
    const res = await getTableObj({dsId: datasourceId.value});
    tableList.value = res.data || [];
  } catch (err: any) {
    console.error('获取表列表失败:', err);
  } finally {
    tableLoading.value = false;
  }
};

// 选择表
const handleSelectTable = async (table: any) => {
  currentTable.value = table;
  tableCommentInput.value = table.customComment || table.tableComment || '';
  await loadFieldList(table.id);
};

// 加载字段列表
const loadFieldList = async (tableId: number) => {
  fieldLoading.value = true;
  try {
    const res = await getFieldObj({tableId: tableId});
    fieldList.value = res.data || [];
  } catch (err: any) {
    console.error('获取字段列表失败:', err);
  } finally {
    fieldLoading.value = false;
  }
};

// 开始编辑表备注
const startEditTableComment = () => {
  if (!currentTable.value) return;
  editingTableComment.value = true;
  tableCommentInput.value = currentTable.value.customComment || currentTable.value.tableComment || '';
};

// 保存表备注
const saveTableComment = async () => {
  if (!currentTable.value) {
    editingTableComment.value = false;
    return;
  }
  const value = tableCommentInput.value || '';
  editingTableComment.value = false;
  currentTable.value.customComment = value;
  try {
    await putTableObj({
      ...currentTable.value,
      customComment: value,
    });
    useMessage().success('表备注修改成功');
  } catch (err: any) {
    useMessage().error(err.msg || '表备注修改失败');
  }
};

// 保存字段修改（自定义注释、数据映射、排序权重）
const handleSaveFields = async () => {
  if (!currentTable.value || fieldList.value.length === 0) {
    return;
  }
  saveLoading.value = true;
  try {
    await Promise.all(
        fieldList.value.map((field) => {
          // 保证携带数据源和表信息
          field.dsId = datasourceId.value;
          field.tableId = currentTable.value.id;
          return putFieldObj(field);
        })
    );
    useMessage().success('保存成功');
    emit('refresh');
  } catch (err: any) {
    useMessage().error(err.msg || '保存失败');
  } finally {
    saveLoading.value = false;
  }
};

// 暴露方法
defineExpose({
  openDialog
});
</script>

<style scoped>
.table-detail-container {
  display: flex;
  height: calc(100vh - 180px);
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.table-list-panel {
  flex: 0 0 320px;
  width: 320px;
  min-width: 320px;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.field-list-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.panel-actions {
  flex-shrink: 0;
}

.panel-title-group {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.table-count {
  font-size: 12px;
  color: #909399;
}

.panel-search {
  padding: 10px 15px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.panel-content {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

.table-base-info {
  padding: 10px 15px 0;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.table-base-row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
  font-size: 13px;
}

.table-base-row .label {
  color: #909399;
  width: 48px;
  flex-shrink: 0;
}

.table-base-row .value {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-base-row .value.editable {
  cursor: pointer;
  max-width: 300px;
}

.table-base-row .value.editable:hover {
  color: #409eff;
}

.table-comment-input {
  flex: 1;
  max-width: 300px;
}

.table-item-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.table-item {
  padding: 10px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.table-item:hover {
  background: #f5f7fa;
}

.table-item.active {
  background: #ecf5ff;
  border-color: #409eff;
}

.table-item-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 4px;
}

.table-item-comment {
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
}

:deep(.el-drawer__body) {
  padding: 10px 20px 20px;
}
</style>
