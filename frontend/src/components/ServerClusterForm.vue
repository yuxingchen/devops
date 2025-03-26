<template>
  <el-dialog
    :title="form.id ? '编辑服务器集群' : '新增服务器集群'"
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    width="600px"
    destroy-on-close
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      @submit.prevent
    >
      <el-form-item label="集群名称" prop="clusterName">
        <el-input v-model="form.clusterName" placeholder="请输入集群名称" />
      </el-form-item>
      <el-form-item label="集群编码" prop="clusterCode">
        <el-input v-model="form.clusterCode" placeholder="请输入集群编码" />
      </el-form-item>
      <el-form-item label="集群IP" prop="clusterIp">
        <el-input v-model="form.clusterIp" placeholder="请输入集群IP" />
      </el-form-item>
      <el-form-item label="集群描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入集群描述"
        />
      </el-form-item>
      <el-form-item label="关联服务器" prop="serverIds">
        <el-select
          v-model="form.serverIds"
          multiple
          placeholder="请选择关联服务器"
          style="width: 100%"
        >
          <el-option
            v-for="server in serverList"
            :key="server.id"
            :label="server.serverName"
            :value="server.id"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {reactive, ref, watch} from 'vue';
import {ElMessage} from 'element-plus';
import {createServerCluster, updateServerCluster} from '@/api/host/serverCluster';
import {listServers} from '@/api/host/server';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  editData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:modelValue', 'success']);

const formRef = ref(null);
const loading = ref(false);
const serverList = ref([]);

// 表单数据
const form = reactive({
  id: undefined,
  clusterName: '',
  clusterCode: '',
  clusterIp: '',
  description: '',
  serverIds: []
});

// 表单校验规则
const rules = {
  clusterName: [
    { required: true, message: '请输入集群名称', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  clusterCode: [
    { required: true, message: '请输入集群编码', trigger: 'blur' },
    { max: 50, message: '长度不能超过50个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '长度不能超过500个字符', trigger: 'blur' }
  ]
};

// 监听编辑数据变化
watch(
  () => props.editData,
  (val) => {
    Object.assign(form, val);
  },
  { deep: true, immediate: true }
);

// 获取服务器列表
const getServerList = async () => {
  try {
    const res = await listServers();
    serverList.value = res.data;
  } catch (error) {
    console.error('获取服务器列表失败:', error);
  }
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    loading.value = true;
    if (form.id) {
      await updateServerCluster(form.id, form);
      ElMessage.success('更新成功');
    } else {
      await createServerCluster(form);
      ElMessage.success('创建成功');
    }
    
    emit('success');
    emit('update:modelValue', false);
  } catch (error) {
    console.error('提交失败:', error);
    ElMessage.error('提交失败');
  } finally {
    loading.value = false;
  }
};

// 初始化
getServerList();
</script> 