<template>
    <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
      :close-on-click-modal="false" draggable>
      <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
       <el-row :gutter="24">
    <el-col :span="12" class="mb20">
      <el-form-item label="文件ID" prop="fileId">
        <el-input v-model="form.fileId" placeholder="请输入文件ID"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="数据库ID" prop="dbId">
        <el-input v-model="form.dbId" placeholder="请输入数据库ID"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="父ID" prop="parentId">
          <el-select v-model="form.parentId" placeholder="请选择父ID">
            <el-option label="请选择">0</el-option>
          </el-select>
        </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="文件名称" prop="filename">
        <el-input v-model="form.filename" placeholder="请输入文件名称"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="文件原名" prop="originalFilename">
        <el-input v-model="form.originalFilename" placeholder="请输入文件原名"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="文件类型" prop="fileType">
        <el-input v-model="form.fileType" placeholder="请输入文件类型"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="文件路径" prop="path">
        <el-input v-model="form.path" placeholder="请输入文件路径"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="minio地址" prop="minioUrl">
        <el-input v-model="form.minioUrl" placeholder="请输入minio地址"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="markdown文件" prop="markdownFile">
        <el-input v-model="form.markdownFile" placeholder="请输入markdown文件"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="状态" prop="status">
        <el-input v-model="form.status" placeholder="请输入状态"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="内容hash值" prop="contentHash">
        <el-input v-model="form.contentHash" placeholder="请输入内容hash值"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="文件大小" prop="fileSize">
        <el-input v-model="form.fileSize" placeholder="请输入文件大小"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="内容类型" prop="contentType">
        <el-input v-model="form.contentType" placeholder="请输入内容类型"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="处理参数" prop="processingParams">
        <el-input v-model="form.processingParams" placeholder="请输入处理参数"/>
      </el-form-item>
      </el-col>

    <el-col :span="12" class="mb20">
      <el-form-item label="是否目录，0否1是" prop="folderFlag">
        <el-input v-model="form.folderFlag" placeholder="请输入是否目录，0否1是"/>
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

<script setup lang="ts" name="KnowledgeFilesDialog">
import { useDict } from '/@/hooks/dict';
import { useMessage } from "/@/hooks/message";
import { getObj, addObj, putObj, validateExist } from '/@/api/agi/knowledgeFiles'
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
	  fileId: '',
	  dbId: '',
	  parentId: '',
	  filename: '',
	  originalFilename: '',
	  fileType: '',
	  path: '',
	  minioUrl: '',
	  markdownFile: '',
	  status: '',
	  contentHash: '',
	  fileSize: '',
	  contentType: '',
	  processingParams: '',
	  folderFlag: '',
});

// 定义校验规则
const dataRules = ref({
    parentId: [{required: true, message: '父ID不能为空', trigger: 'blur'}],
    filename: [{required: true, message: '文件名称不能为空', trigger: 'blur'}],
    folderFlag: [{required: true, message: '是否目录，0否1是不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
	nextTick(() => {
		dataFormRef.value?.resetFields();
	});

  // 获取knowledgeFiles信息
  if (id) {
    form.id = id
    getKnowledgeFilesData(id)
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
const getKnowledgeFilesData = (id: string) => {
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