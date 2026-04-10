<template>
    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="智能体ID" prop="agentId">
        <el-input v-model="form.agentId" placeholder="请输入智能体ID"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入知识库名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库描述" prop="description">
        <el-input v-model="form.description" placeholder="请输入知识库描述"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库图标" prop="icon">
        <el-input v-model="form.icon" placeholder="请输入知识库图标"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="知识库图片" prop="pics">
        <el-input v-model="form.pics" placeholder="请输入知识库图片"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="案例信息" prop="examples">
        <el-input v-model="form.examples" placeholder="请输入案例信息"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="json配置" prop="configJson">
        <el-input v-model="form.configJson" placeholder="请输入json配置信息"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="是否默认" prop="defaultFlag">
        <el-radio-group v-model="form.defaultFlag">
          <el-radio border value="0">否</el-radio>
          <el-radio border value="1">是</el-radio>
        </el-radio-group>
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

<script setup lang="ts" name="AgentConfigsDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/agi/agentConfigs'
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
	  agentId: '',
	  name: '',
	  description: '',
	  icon: '',
	  pics: '',
	  examples: '',
	  configJson: '',
	  defaultFlag: '0',
	  createdBy: '',
	  updatedBy: '',
	  createdAt: '',
	  updatedAt: '',
});

// 定义校验规则
const dataRules = ref({
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取agentConfigs信息
  if (id) {
    form.id = id
    getAgentConfigsData(id)
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
const getAgentConfigsData = (id: string) => {
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
