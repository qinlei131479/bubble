<template>
    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="数据库ID" prop="dbId">
          <el-select v-model="form.dbId" placeholder="请选择数据库ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入知识库名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库描述" prop="description">
        <el-input type="textarea" v-model="form.description" placeholder="请输入知识库描述"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库类型" prop="kbType">
        <el-input v-model="form.kbType" placeholder="请输入知识库类型"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="向量信息" prop="embedInfo">
        <el-input type="textarea" v-model="form.embedInfo" placeholder="请输入向量信息"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="大模型信息" prop="llmInfo">
        <el-input type="textarea" v-model="form.llmInfo" placeholder="请输入大模型信息"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="查询参数" prop="queryParams">
        <el-input type="textarea" v-model="form.queryParams" placeholder="请输入查询参数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="扩展参数" prop="additionalParams">
        <el-input type="textarea" v-model="form.additionalParams" placeholder="请输入扩展参数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="分享配置" prop="shareConfig">
        <el-input type="textarea" v-model="form.shareConfig" placeholder="请输入分享配置"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label=" mindmap" prop="mindmap">
        <el-input v-model="form.mindmap" placeholder="请输入 mindmap"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="样例问题" prop="sampleQuestions">
        <el-input type="textarea" v-model="form.sampleQuestions" placeholder="请输入样例问题"/>
      </el-form-item>
      </el-col>

			</el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="visible = false">取 消</el-button>
          <el-button type="primary" @click="onSubmit" :disabled="loading">确 认</el-button>
        </span>
      </template>
    </el-dialog>
</template>

<script setup lang="ts" name="KnowledgeBasesDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/agi/knowledgeBases'
import { rule } from '/@/utils/validate';
const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
// 定义字典

// 提交表单数据
const form = reactive({
		id:'',
	  dbId: '',
	  name: '',
	  description: '',
	  kbType: '',
	  embedInfo: '',
	  llmInfo: '',
	  queryParams: '',
	  additionalParams: '',
	  shareConfig: '',
	  mindmap: '',
	  sampleQuestions: '',
});

// 定义校验规则
const dataRules = ref({
    dbId: [{required: true, message: '数据库ID不能为空', trigger: 'blur'}],
    name: [{required: true, message: '知识库名称不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取knowledgeBases信息
  if (id) {
    form.id = id
    getKnowledgeBasesData(id)
  }
};

// 提交
const onSubmit = async () => {
	const valid = await dataFormRef.value.validate().catch(() => {});
	if (!valid) return false;

	try {
    loading.value = true;
		form.id ? await putObj(form) : await addObj(form);
		useMessage().success(form.id ? '修改成功' : '添加成功');
		visible.value = false;
		emit('refresh');
	} catch (err: any) {
		useMessage().error(err.msg);
	} finally {
    loading.value = false;
  }
};


// 初始化表单数据
const getKnowledgeBasesData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({id: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};

// 暴露变量
defineExpose({
  openDialog
});
</script>