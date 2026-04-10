<template>
  <div class="layout-padding">
    <div class="layout-padding-auto layout-padding-view">
      <el-row>
        <div class="mb8" style="width: 100%">
          <el-button plain :disabled="multiple" icon="Delete" type="primary"
                     v-auth="'agi_conversationStats_del'" @click="handleDelete(selectObjs)">
            删 除
          </el-button>
          <right-toolbar v-model:showSearch="showSearch" :export="'agi_conversationStats_export'"
                         @exportExcel="exportExcel" class="ml10 mr20" style="float: right;"
                         @queryTable="getDataList"></right-toolbar>
        </div>
      </el-row>
      <el-table :data="state.dataList" v-loading="state.loading" border
                :cell-style="tableStyle.cellStyle" :header-cell-style="tableStyle.headerCellStyle"
                @selection-change="selectionChangHandle"
                @sort-change="sortChangeHandle">
        <el-table-column type="selection" width="40" align="center"/>
        <el-table-column type="index" label="#" width="40"/>
        <el-table-column prop="conversationId" label="对话ID" show-overflow-tooltip/>
        <el-table-column prop="messageCount" label="消息总数" show-overflow-tooltip/>
        <el-table-column prop="totalTokens" label="统计tokens" show-overflow-tooltip/>
        <el-table-column prop="usedModel" label="使用模型" show-overflow-tooltip/>
        <el-table-column prop="userFeedback" label="用户反馈信息" show-overflow-tooltip/>
        <el-table-column prop="createdAt" label="创建时间" show-overflow-tooltip/>
        <el-table-column prop="updatedAt" label="更新时间" show-overflow-tooltip/>
        <!--        <el-table-column label="操作" width="150">-->
        <!--          <template #default="scope">-->
        <!--            <el-button icon="edit-pen" text type="primary" v-auth="'agi_conversationStats_edit'"-->
        <!--              @click="formDialogRef.openDialog(scope.row.id)">编辑</el-button>-->
        <!--            <el-button icon="delete" text type="primary" v-auth="'agi_conversationStats_del'" @click="handleDelete([scope.row.id])">删除</el-button>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
      </el-table>
      <pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" v-bind="state.pagination"/>
    </div>

    <!-- 编辑、新增  -->
    <form-dialog ref="formDialogRef" @refresh="getDataList(false)"/>

    <!-- 导入excel (需要在 upms-biz/resources/file 下维护模板) -->
    <upload-excel
        ref="excelUploadRef"
        title="导入"
        url="/agi/conversationStats/import"
        temp-url="/admin/sys-file/local/file/conversationStats.xlsx"
        @refreshDataList="getDataList"
    />
  </div>
</template>

<script setup lang="ts" name="systemConversationStats">
import {BasicTableProps, useTable} from "/@/hooks/table";
import {fetchList, delObjs} from "/@/api/agi/conversationStats";
import {useMessage, useMessageBox} from "/@/hooks/message";
import {useDict} from '/@/hooks/dict';

// 引入组件
const FormDialog = defineAsyncComponent(() => import('./form.vue'));
// 定义查询字典

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
  downBlobFile('/agi/conversationStats/export', Object.assign(state.queryForm, {ids: selectObjs}), 'conversationStats.xlsx')
}

// 多选事件
const selectionChangHandle = (objs: { id: string }[]) => {
  selectObjs.value = objs.map(({id}) => id);
  //multiple.value = !objs.length;
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