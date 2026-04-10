<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
          <el-form-item label="数据源" prop="dsId">
            <el-select class="w100" clearable placeholder="请选择数据源" v-model="state.queryForm.dsId">
              <el-option :key="item.id" :label="item.name" :value="item.id"
                         v-for="item in datasourceData"/>
            </el-select>
          </el-form-item>
          <el-form-item label="是否启用" prop="enabledFlag">
            <el-radio-group v-model="state.queryForm.enabledFlag">
              <el-radio :value="item.value" v-for="(item, index) in yes_no_type" border :key="index">{{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <el-button icon="search" type="primary" @click="getDataList">查 询</el-button>
            <el-button icon="Refresh" @click="resetQuery">重 置</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-row>
        <div class="mb8" style="width: 100%">
          <el-button icon="folder-add" type="primary" class="ml10" @click="formDialogRef.openDialog()"
                     v-auth="'agi_sqlTrain_add'">
            新 增
          </el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
                     v-auth="'agi_sqlTrain_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'agi_sqlTrain_export'"
                         @exportExcel="exportExcel" class="ml10 mr20" style="float: right;"
                         @queryTable="getDataList"></right-toolbar>
        </div>
      </el-row>
      <el-table :data="state.dataList" v-loading="state.loading" border
                :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle"
                @selection-change="selectionChangHandle"
                @sort-change="sortChangeHandle">
        <el-table-column type="selection" width="40" align="center"/>
        <el-table-column type="index" label="#" width="50"/>
        <el-table-column prop="dsName" label="数据源" width="150"/>
        <el-table-column prop="question" label="问题描述" show-overflow-tooltip/>
        <el-table-column prop="description" label="示例SQL" show-overflow-tooltip/>
        <el-table-column prop="enabledFlag" label="是否启用" width="100">
          <template #default="scope">
            <dict-tag :options="yes_no_type" :value="scope.row.enabledFlag"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" sortable="custom" width="200"/>
        <el-table-column prop="updateTime" label="更新时间" width="200"/>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'agi_sqlTrain_edit'"
                       @click="formDialogRef.openDialog(scope.row.id)">编辑
            </el-button>
            <el-button icon="delete" text type="primary" v-auth="'agi_sqlTrain_del'"
                       @click="handleDelete([scope.row.id])">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" v-bind="state.pagination"/>
    </div>

    <!-- 编辑、新增  -->
    <form-dialog ref="formDialogRef" @refresh="getDataList(false)"/>

    <!-- 导入excel (需要在 upms-biz/resources/file 下维护模板) -->
    <upload-excel
        ref="excelUploadRef"
        title="导入"
        url="/agi/sqlTrain/import"
        temp-url="/admin/sys-file/local/file/sqlTrain.xlsx"
        @refreshDataList="getDataList"
    />
  </div>
</template>

<script setup lang="ts" name="systemSqlTrain">
import {BasicTableProps, useTable} from "/@/hooks/table";
import {fetchList, delObjs} from "/@/api/agi/sqlTrain";
import {getObj as getDatasource } from "/@/api/agi/datasource";
import {useMessage, useMessageBox} from "/@/hooks/message";
import {useDict} from '/@/hooks/dict';
import {getSupplierObj} from "/@/api/agi/supplierModel";

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典
const {yes_no_type} = useDict('yes_no_type')
// 定义变量内容
const formDialogRef = ref()
const excelUploadRef = ref();
// 搜索变量
const queryRef = ref()
const showSearch = ref(true)
// 多选变量
const selectObjs = ref([]) as any
const multiple = ref(true)

const state: BasicTableProps = reactive<BasicTableProps>({
  queryForm: {},
  pageList: fetchList
})

//  table hook
const {
  getDataList,
  currentChangeHandle,
  sizeChangeHandle,
  sortChangeHandle,
  downBlobFile,
  tableStyle
} = useTable(state)

// 清空搜索条件
const resetQuery = () => {
  // 清空搜索条件
  queryRef.value?.resetFields()
  // 清空多选
  selectObjs.value = []
  getDataList()
}

// 导出excel
const exportExcel = () => {
  downBlobFile('/agi/sqlTrain/export', Object.assign(state.queryForm, {ids: selectObjs}), 'sqlTrain.xlsx')
}

// 多选事件
const selectionChangHandle = (objs: { id: string }[]) => {
  selectObjs.value = objs.map(({id}) => id);
  multiple.value = !objs.length;
};

// 删除操作
const handleDelete = async (ids: string[]) => {
  try {
    await useMessageBox().confirm('此操作将永久删除');
  } catch {
    return;
  }

  try {
    await delObjs(ids);
    getDataList();
    useMessage().success('删除成功');
  } catch (err: any) {
    useMessage().error(err.msg);
  }
};


const datasourceData = ref<any[]>([]);

// 初始化
onMounted(() => {
  // 获取数据源
  getDatasource({}).then((res) => {
    datasourceData.value = res.data;
  });
});
</script>