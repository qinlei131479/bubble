<template>
  <el-dialog :title="form.id ? '编辑' : '新增'" v-model="visible"
             :close-on-click-modal="false" draggable>
    <el-form ref="dataFormRef" :model="form" :rules="dataRules" formDialogRef label-width="120px" v-loading="loading">
      <el-row :gutter="24">
        <el-col :span="24" class="mb20">
          <el-form-item label="术语名称" prop="word">
            <el-input v-model="form.word" placeholder="请输入术语名称"/>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="同义词" prop="words">
            <el-tag :key="tag" v-for="tag in form.words" closable class="button-new-tag"
                    :disable-transitions="false" @close="handleClose(tag)">
              {{ tag }}
            </el-tag>
            <el-input class="input-new-tag" v-if="inputVisible"
                      v-model="inputValue" ref="saveTagInputRef" size="small"
                      @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"
            >
            </el-input>
            <el-button v-if="form.words.length<3" class="button-new-tag" size="small" @click="showInput">
              + New Tag
            </el-button>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="术语描述" prop="description">
            <el-input type="textarea" v-model="form.description" placeholder="请输入术语描述"/>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="指定数据源" prop="specificDs">
            <el-radio-group v-model="form.specificDs">
              <el-radio :key="index" :value="item.value" border v-for="(item, index) in yes_no_type">
                {{ item.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20" v-if="form.specificDs ==='1'">
          <el-form-item label="数据源" prop="datasourceIds">
            <el-select v-model="form.datasourceIds" clearable placeholder="请选择数据源">
              <el-option :key="item.id" :label="item.name" :value="item.id"
                         v-for="item in datasourceData"/>
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :span="24" class="mb20">
          <el-form-item label="是否启用" prop="enabledFlag">
            <el-radio-group v-model="form.enabledFlag">
              <el-radio :key="index" :value="item.value" border v-for="(item, index) in yes_no_type">
                {{ item.label }}
              </el-radio>
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

<script setup lang="ts" name="TerminologyDialog">
import {useDict} from '/@/hooks/dict';
import {useMessage} from "/@/hooks/message";
import {getObj, addObj, putObj, validateExist} from '/@/api/agi/terminology'
import {rule} from '/@/utils/validate';
import {getObj as getDatasource} from "/@/api/agi/datasource";
import {InputInstance} from "element-plus";

const emit = defineEmits(['refresh']);

// 定义变量内容
const dataFormRef = ref();
const visible = ref(false)
const loading = ref(false)

const inputVisible = ref(false)
const inputValue = ref('')
// 定义字典
const {yes_no_type} = useDict('yes_no_type');
// 提交表单数据
const form = reactive({
  id: '',
  parentId: '',
  word: '',
  words: [] as string[],
  description: '',
  specificDs: '0',
  embedding: '',
  datasourceIds: '',
  enabledFlag: '1',
});

// 定义校验规则
const dataRules = ref({
  word: [{required: true, message: '名称为空', trigger: 'blur'}],
  specificDs: [{required: true, message: '指定数据源不能为空', trigger: 'blur'}],
  enabledFlag: [{required: true, message: '是否启用不能为空', trigger: 'blur'}, {
    validator: rule.number,
    trigger: 'blur'
  }],
})

// 打开弹窗
const openDialog = (id: string) => {
  visible.value = true
  form.id = ''

  // 重置表单数据
  nextTick(() => {
    dataFormRef.value?.resetFields();
  });
  getDatasourceData()
  // 获取terminology信息
  if (id) {
    form.id = id
    getTerminologyData(id)
  }
};

const handleClose = (tag: string) => {
  form.words.splice(form.words.indexOf(tag), 1)
};

const saveTagInputRef = ref<InputInstance>()

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    saveTagInputRef.value!.input!.focus()
  })
};

const handleInputConfirm = () => {
  if (inputValue.value) {
    form.words.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
};


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
const getTerminologyData = (id: string) => {
  // 获取数据
  loading.value = true
  getObj({id: id}).then((res: any) => {
    Object.assign(form, res.data[0])
  }).finally(() => {
    loading.value = false
  })
};


const datasourceData = ref<any[]>([]);

const getDatasourceData = () => {
  getDatasource({}).then((res) => {
    datasourceData.value = res.data;
  });
}
// 暴露变量
defineExpose({
  openDialog,
  handleClose,
  showInput,
  handleInputConfirm
});
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}

.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}
</style>