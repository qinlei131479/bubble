<template>
  <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
             :close-on-click-modal="false" draggable>
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="90px" v-loading="loading">
      <el-row :gutter="24">
        <el-col :span="12" class="mb20">
          <el-form-item label="供应商" prop="supplierId">
            <el-select class="w100" clearable placeholder="请选择供应商" v-model="form.supplierId"
                       @change="fillData">
              <el-option :key="item.id" :label="item.name + ' ['+item.description+']'" :value="item.id"
                         v-for="item in supplierData"/>
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="12" class="mb20">
          <el-form-item label="模型类型" prop="modelType">
            <el-select class="w100" clearable placeholder="请选择模型类型" v-model="form.modelType">
              <el-option :key="item.value" :label="item.label" :value="item.value"
                         v-for="item in agi_supplier_model_type"/>
            </el-select>
          </el-form-item>
        </el-col>

        <template v-if="form.supplierId">
          <el-col :span="12" class="mb20">
            <el-form-item label="API Key" prop="apiKey">
              <el-input v-model="form.apiKey" placeholder="请输入模型名称"/>
            </el-form-item>
          </el-col>

          <el-col :span="12" class="mb20">
            <el-form-item label="API域名" prop="apiDomain">
              <el-input v-model="form.apiDomain" placeholder="请输入API Url"/>
            </el-form-item>
          </el-col>
        </template>

        <el-col :span="12" class="mb20">
          <el-form-item label="模型名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入模型名称"/>
          </el-form-item>
        </el-col>

        <el-col :span="12" class="mb20">
          <el-form-item label="模型别名" prop="baseModel">
            <el-input v-model="form.baseModel" placeholder="请输入模型名称（别名）"/>
          </el-form-item>
        </el-col>

        <el-col :span="12" class="mb20">
          <el-form-item label="默认模型" prop="defaultFlag">
            <el-radio-group v-model="form.defaultFlag">
              <el-radio :key="index" :value="item.value" border v-for="(item, index) in yes_no_type">
                {{ item.label}}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="12" class="mb20">
          <el-form-item label="上下文长度" prop="contextLength">
            <el-input v-model="form.contextLength" placeholder="请输入模型上下文长度"/>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="扩展配置" prop="extConfig">
            <el-input v-model="form.extConfig" placeholder="请输入模型扩展配置" type="textarea"/>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="模型描述" prop="description">
            <el-input v-model="form.description" placeholder="请输入模型描述" type="textarea"/>
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

<script setup lang="ts" name="SupplierModelDialog">
import {useDict} from '/@/hooks/dict';
import {useMessage} from "/@/hooks/message";
import {getObj, addObj, putObj, validateExist, getSupplierObj} from '/@/api/agi/supplierModel'
import {rule} from '/@/utils/validate';

const emit = defineEmits(['refresh']);

const {agi_supplier_model_type, yes_no_type} = useDict('agi_supplier_model_type', 'yes_no_type');
// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)
const supplierData = ref<any[]>([]);
// 定义字典

// 提交表单数据
const form = reactive({
  id: '',
  supplierId: '',
  name: '',
  baseModel: '',
  modelType: '1',
  contextLength: '128K',
  description: '',
  defaultFlag: '0',
  extConfig: '{}',
  apiKey: '',
  apiDomain: ''
});

// 定义校验规则
const dataRules = ref({
  supplierId: [{required: true, message: '供应商不能为空', trigger: 'blur'}],
  name: [{required: true, message: '模型姓名不能为空', trigger: 'blur'}],
  apiKey: [{required: true, message: 'API key不能为空', trigger: 'blur'}],
  apiDomain: [{required: true, message: 'API域名不能为空', trigger: 'blur'}],
  baseModel: [{required: true, message: '模型别名不能为空', trigger: 'blur'}],
  modelType: [{required: true, message: '模型类型不能为空', trigger: 'blur'}],
  defaultFlag: [{required: true, message: '默认模型不能为空', trigger: 'blur'}],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });

  getSupplierData()
  // 获取supplierModel信息
  if (id) {
    form.id = id
    getSupplierModelData(id)
  }
};

const fillData = (selectedId: string) => {
  const selectedItem = supplierData.value.find(item => item.id === selectedId);
  form.apiKey = selectedItem.apiKey;
  form.apiDomain = selectedItem.apiDomain;
}

// 提交
const onSubmit = async () => {
  const valid = await dataFormRef.value.validate().catch(() => {
  });
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
const getSupplierModelData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({id: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};


// 初始化供应商数据
const getSupplierData = () => {
  // 获取供应商数据
  getSupplierObj({status: 0}).then((res) => {
    supplierData.value = res.data;
    // 默认选择第一个
    // form.supplierId = res.data[0].id;
  });
};

// 暴露变量
defineExpose({
  openDialog,
  fillData
});
</script>