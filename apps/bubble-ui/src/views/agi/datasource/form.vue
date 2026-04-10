<template>
  <el-dialog :title="form.id ? '编辑数据源' : '新增数据源'" v-model="visible"
             :close-on-click-modal="false" draggable width="900px">
    <!-- 步骤条 -->
    <div class="steps-container">
      <el-steps style="max-width: 600px" :active="activeStep" align-center>
        <el-step title="连接配置" description="配置数据库连接信息"/>
        <el-step title="选择表" description="选择需要管理的表"/>
      </el-steps>
    </div>

    <!-- 第一步：连接配置 -->
    <div v-show="activeStep === 0" class="step-content">
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12" class="mb20">
            <el-form-item label="数据源名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入数据源名称"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="数据库类型" prop="dsType">
              <el-select v-model="form.dsType" placeholder="请选择类型" @change="handleTypeChange"
                         :disabled="form.id!=''">
                <el-option :key="item.value" :label="item.label" :value="item.value"
                           v-for="item in aig_ds_type"/>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="24" class="mb20">
            <el-form-item label="描述" prop="description">
              <el-input type="textarea" v-model="form.description" placeholder="请输入描述"/>
            </el-form-item>
          </el-col>

          <el-divider>连接配置</el-divider>

          <el-col :span="12" class="mb20">
            <el-form-item label="主机" prop="host">
              <el-input v-model="form.host" placeholder="请输入主机"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="端口" prop="port">
              <el-input v-model="form.port" placeholder="请输入端口"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入用户名"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入密码" show-password/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="数据库名称" prop="dsName">
              <el-input v-model="form.dsName" placeholder="请输入数据库名称"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="额外参数" prop="extraJdbc">
              <el-input v-model="form.extraJdbc" placeholder="如: useSSL=false&serverTimezone=UTC"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 第二步：选择表 -->
    <div v-show="activeStep === 1" class="step-content">
      <div class="table-selection">
        <!-- 搜索和统计 -->
        <div class="table-header">
          <div class="table-stats">
            <span>共 <strong>{{ tableList.length }}</strong> 张表</span>
            <span class="ml20">已选择 <strong>{{ selectedTables.length }}</strong> 张表</span>
          </div>
          <el-input v-model="tableSearch" placeholder="请输入表名搜索" clearable style="width: 250px">
            <template #prefix>
              <el-icon>
                <Search/>
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 表列表 -->
        <div v-loading="tableLoading" class="table-list-wrapper">
          <el-empty v-if="filteredTableList.length === 0 && !tableLoading"
                    description="暂无表数据，请先在第一步填写数据源信息"></el-empty>
          <el-table ref="tableRef" v-else :data="filteredTableList" @selection-change="handleTableSelectionChange"
                    border height="350">
            <el-table-column type="selection" width="50"/>
            <el-table-column prop="tableName" label="表名" min-width="180" show-overflow-tooltip/>
            <el-table-column prop="tableComment" label="表注释" min-width="180" show-overflow-tooltip/>
            <el-table-column prop="tableType" label="表类型" width="100"/>
          </el-table>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <div class="footer-left">
          <el-button type="success" :loading="testLoading" @click="handleTestConnection" v-if="activeStep === 0">
            {{ testLoading ? '测试中...' : '测试连接' }}
          </el-button>
        </div>
        <div class="footer-right">
          <el-button @click="visible = false">取 消</el-button>
          <el-button v-if="activeStep === 0" type="primary" @click="handleNextStep">
            下一步
          </el-button>
          <template v-else>
            <el-button @click="activeStep = 0">上一步</el-button>
            <el-button type="primary" :loading="loading" @click="onSubmit">
              确 定
            </el-button>
          </template>
        </div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts" name="DatasourceDialog">
import {useDict} from '/@/hooks/dict';
import {useMessage} from "/@/hooks/message";
import {
  getObj,
  addObj,
  putObj,
  testConnection,
  fetchTablesByConf
} from '/@/api/agi/datasource'
import {Connection, Search} from '@element-plus/icons-vue';

const emit = defineEmits(['refresh']);

// 数据源类型默认端口映射
const defaultPorts: Record<string, number> = {
  mysql: 3306,
  pg: 5432,
  oracle: 1521,
  sqlServer: 1433,
  ck: 8123,
  dm: 5236,
  doris: 9030,
  starrocks: 9030
};

// 定义变量内容
const dataFormRef = ref();
const tableRef = ref();
const visible = ref(false)
const loading = ref(false)
const testLoading = ref(false)
const activeStep = ref(0)

// 选择表相关
const tableLoading = ref(false)
const tableList = ref<any[]>([])
const selectedTables = ref<any[]>([])
const tableSearch = ref('')

// 定义字典
const {aig_ds_type} = useDict('aig_ds_type')

// 提交表单数据
const form = reactive({
  id: '',
  name: '',
  url: '',
  username: '',
  password: '',
  dsType: '',
  confType: '',
  dsName: '',
  instance: '',
  port: 3306,
  host: '',
  description: '',
  extraJdbc: '',
  tableNames: []
});

// 定义校验规则
const dataRules = ref({
  name: [{required: true, message: '数据源名称不能为空', trigger: 'blur'}],
  username: [{required: true, message: '用户名不能为空', trigger: 'blur'}],
  password: [{required: true, message: '密码不能为空', trigger: 'blur'}],
  dsType: [{required: true, message: '数据库类型不能为空', trigger: 'change'}],
  port: [{required: true, message: '端口不能为空', trigger: 'blur'}],
  host: [{required: true, message: '主机不能为空', trigger: 'blur'}],
})

// 过滤后的表列表（支持搜索）
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

// 数据源类型变化时自动填充端口
const handleTypeChange = (val: string) => {
  if (defaultPorts[val]) {
    form.port = defaultPorts[val];
  }
};

// 测试数据库连接
const handleTestConnection = async () => {
  if (!form.host || !form.port || !form.dsType) {
    useMessage().warning('请先填写主机、端口和数据库类型');
    return;
  }

  testLoading.value = true;
  try {
    const res = await testConnection({
      id: form.id || null,
      dsType: form.dsType,
      host: form.host,
      port: form.port,
      username: form.username,
      password: form.password,
      dsName: form.dsName,
      extraJdbc: form.extraJdbc
    });
    if (res.data?.connected) {
      useMessage().success(`连接成功！数据库: ${res.data.databaseName}，版本: ${res.data.databaseVersion}`);
      // 连接成功后自动获取表列表
      handleFetchTables();
    } else {
      useMessage().error(res.data?.errorMessage || '连接失败');
    }
  } catch (err: any) {
    useMessage().error(err.msg || '测试连接失败');
  } finally {
    testLoading.value = false;
  }
};

// 获取数据库表列表
const handleFetchTables = async () => {
  if (!form.host || !form.port || !form.dsType) {
    return;
  }

  tableLoading.value = true;
  try {
    const res = await fetchTablesByConf({
      id: form.id || null,
      dsType: form.dsType,
      host: form.host,
      port: form.port,
      username: form.username,
      password: form.password,
      dsName: form.dsName,
      extraJdbc: form.extraJdbc
    });
    tableList.value = res.data || [];

    // 如果有已选择的表名，设置选中状态
    if (form.tableNames && form.tableNames.length > 0) {
      nextTick(() => {
        setTableSelection();
      });
    }
  } catch (err: any) {
    useMessage().error(err.msg || '获取表列表失败');
  } finally {
    tableLoading.value = false;
  }
};

// 根据form.tableNames设置表格选中状态
const setTableSelection = () => {
  if (!form.tableNames || form.tableNames.length === 0 || !tableRef.value) {
    return;
  }

  const tableNameSet = new Set(form.tableNames);
  const rowsToSelect: any[] = [];

  tableList.value.forEach((table) => {
    if (tableNameSet.has(table.tableName)) {
      rowsToSelect.push(table);
    }
  });

  // 选中对应的行
  rowsToSelect.forEach((row) => {
    tableRef.value.toggleRowSelection(row, true);
  });

  // 同时更新selectedTables
  selectedTables.value = rowsToSelect;
};

// 表选择变化
const handleTableSelectionChange = (selection: any[]) => {
  console.log(selection)
  selectedTables.value = selection;
};

// 下一步
const handleNextStep = async () => {
  // 先验证表单
  const valid = await dataFormRef.value.validate().catch(() => {
  });
  if (!valid) return false;

  // 如果还没有获取过表数据，则自动获取
  if (tableList.value.length === 0) {
    await handleFetchTables();
  } else if (form.tableNames && form.tableNames.length > 0) {
    // 如果已有表数据但是在编辑模式下，需要重新设置选中状态
    nextTick(() => {
      setTableSelection();
    });
  }

  // 切换到第二步
  activeStep.value = 1;
};

// 提交
const onSubmit = async () => {
  try {
    loading.value = true;

    // 将选中的表名赋值给form.tableNames
    if (selectedTables.value.length > 0) {
      form.tableNames = selectedTables.value.map(t => t.tableName);
    } else {
      form.tableNames = [];
    }

    let currentId = form.id;

    if (form.id) {
      // 编辑模式
      await putObj(form);
      useMessage().success('修改成功');
    } else {
      // 新增模式
      const res = await addObj(form);
      useMessage().success('添加成功');

      // 从返回结果中获取新创建的ID
      if (res.data && res.data.id) {
        currentId = res.data.id;
        form.id = currentId;
      }
    }

    // 刷新列表
    emit('refresh');

    visible.value = false;
  } catch (err: any) {
    useMessage().error(err.msg);
  } finally {
    loading.value = false;
  }
};

// 打开弹窗
const openDialog = (id?: string) => {
  visible.value = true
  activeStep.value = 0
  form.id = ''

  // 重置表相关数据
  tableList.value = []
  selectedTables.value = []
  tableSearch.value = ''

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  // 获取datasource信息
  if (id) {
    form.id = id
    getDatasourceData(id)
  }
};

// 初始化表单数据
const getDatasourceData = (id: string) => {
  loading.value = true
  getObj({id: id}).then((res: any) => {
    if (res.data && res.data.length > 0) {
      Object.assign(form, res.data[0])
      // 编辑模式下，如果端口是数字，转为字符串
    }
  }).finally(() => {
    loading.value = false
  })
};

// 暴露变量
defineExpose({
  openDialog
});
</script>

<style scoped>
.steps-container {
  padding: 20px 0 30px;
  margin-bottom: 10px;
}

.steps-container :deep(.el-steps) {
  max-width: 400px;
  margin: 0 auto;
}

.steps-container :deep(.el-step__title) {
  font-size: 14px;
}

.steps-container :deep(.el-step__title.is-process) {
  color: #409eff;
  font-weight: 600;
}

.steps-container :deep(.el-step__title.is-finish) {
  color: #67c23a;
}

.steps-container :deep(.el-step__head.is-process) {
  color: #409eff;
  border-color: #409eff;
}

.steps-container :deep(.el-step__head.is-finish) {
  color: #67c23a;
  border-color: #67c23a;
}

.steps-container :deep(.el-step__icon) {
  width: 28px;
  height: 28px;
  font-size: 12px;
}

.steps-container :deep(.el-step__icon.is-status-process) {
  background-color: #409eff;
  border-color: #409eff;
  color: #fff;
}

.steps-container :deep(.el-step__icon.is-status-finish) {
  background-color: #67c23a;
  border-color: #67c23a;
  color: #fff;
}

.steps-container :deep(.el-step__line) {
  background-color: #e4e7ed;
}

.steps-container :deep(.el-step__line.is-horizontal) {
  background-color: #e4e7ed;
}

.step-content {
  min-height: 300px;
}

.table-selection {
  padding: 10px 0;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 12px 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.table-stats {
  color: #606266;
  font-size: 14px;
}

.table-stats strong {
  color: #409eff;
  font-size: 16px;
}

.ml20 {
  margin-left: 20px;
}

.table-list-wrapper {
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.footer-left {
  display: flex;
  align-items: center;
  padding-left: 50px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
