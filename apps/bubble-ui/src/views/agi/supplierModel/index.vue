<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row v-show="showSearch">
        <el-form :model="state.queryForm" ref="queryRef" :inline="true" @keyup.enter="getDataList">
          <el-form-item label="模型名称" prop="name">
            <el-input placeholder="请输入模型名称" v-model="state.queryForm.name"/>
          </el-form-item>
          <el-form-item label="供应商" prop="supplierId">
            <el-select class="w100" clearable placeholder="请选择供应商" v-model="state.queryForm.supplierId">
              <el-option :key="item.id" :label="item.name + ' ['+item.description+']'" :value="item.id"
                         v-for="item in supplierData"/>
            </el-select>
          </el-form-item>
          <el-form-item label="模型类型" prop="modelType">
            <el-select v-model="state.queryForm.modelType" placeholder="请选择模型类型">
              <el-option :key="item.value" :label="item.label" :value="item.value"
                         v-for="item in agi_supplier_model_type"/>
            </el-select>
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
                     v-auth="'agi_supplierModel_add'">
            新 增
          </el-button>
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
                     v-auth="'agi_supplierModel_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'agi_supplierModel_export'"
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
        <el-table-column prop="supplierName" label="供应商" width="180"/>
        <el-table-column prop="name" label="模型名称" width="220"/>
        <el-table-column prop="baseModel" label="模型别名" width="220"/>
        <el-table-column prop="modelType" label="模型类型" width="120">
          <template #default="scope">
            <dict-tag :options="agi_supplier_model_type" :value="scope.row.modelType"></dict-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contextLength" label="上下文长度" width="120"/>
        <el-table-column prop="description" label="模型描述" show-overflow-tooltip/>
        <!--        <el-table-column prop="defaultFlag" label="默认模型" show-overflow-tooltip>-->
        <!--          <template #default="scope">-->
        <!--            <dict-tag :options="yes_no_type" :value="scope.row.defaultFlag"></dict-tag>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
        <!--        <el-table-column prop="extConfig" label="扩展配置" show-overflow-tooltip/>-->
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button icon="edit-pen" text type="primary" v-auth="'agi_supplierModel_edit'"
                       @click="formDialogRef.openDialog(scope.row.id)">编辑
            </el-button>
            <el-button icon="delete" text type="primary" v-auth="'agi_supplierModel_del'"
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
        url="/agi/supplierModel/import"
        temp-url="/admin/sys-file/local/file/supplierModel.xlsx"
        @refreshDataList="getDataList"
    />
  </div>
</template>

<script setup lang="ts" name="systemSupplierModel">
import {BasicTableProps, useTable} from "/@/hooks/table";
import {fetchList, delObjs, getSupplierObj} from "/@/api/agi/supplierModel";
import {useMessage, useMessageBox} from "/@/hooks/message";
import {useDict} from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典
const {agi_supplier_model_type, yes_no_type} = useDict('agi_supplier_model_type', 'yes_no_type');
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

const supplierData = ref<any[]>([]);

// 初始化供应商数据
onMounted(() => {
  // 获取供应商数据
  getSupplierObj({status: 0}).then((res) => {
    supplierData.value = res.data;
  });
});
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
  downBlobFile('/agi/supplierModel/export', Object.assign(state.queryForm, {ids: selectObjs}), 'supplierModel.xlsx')
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
</script>