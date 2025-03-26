<template>
  <div class="app-container">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增环境
      </el-button>
    </div>

    <el-table v-loading="loading" :data="envList" border style="width: 100%">
      <el-table-column prop="envName" label="环境名称" />
      <el-table-column prop="envCode" label="环境编码" />
      <el-table-column prop="description" label="环境描述" show-overflow-tooltip />
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="warning" size="small" @click="handleEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      @close="resetForm"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="padding: 10px"
      >
        <el-form-item label="环境名称" prop="envName">
          <el-input v-model="form.envName" placeholder="请输入环境名称" />
        </el-form-item>
        <el-form-item label="环境编码" prop="envCode">
          <el-input v-model="form.envCode" placeholder="请输入环境编码" />
        </el-form-item>
        <el-form-item label="环境描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            rows="3"
            placeholder="请输入环境描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {computed, onMounted, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {createEnv, deleteEnv, listEnvsWithParams, updateEnv} from '@/api/host/env'
import {formatDateTime} from '@/utils/format'
import {useUserStore} from '@/store/modules/user'

// 环境列表数据
const envList = ref([])
const loading = ref(false)
const userStore = useUserStore()

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // add or edit
const dialogTitle = computed(() => dialogType.value === 'add' ? '新增环境' : '编辑环境')
const formRef = ref(null)
const submitting = ref(false)

// 表单数据
const form = ref({
  envName: '',
  envCode: '',
  description: '',
})

// 表单校验规则
const rules = {
  envName: [
    { required: true, message: '请输入环境名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  envCode: [
    { required: true, message: '请输入环境编码', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '只能包含字母、数字、下划线和中划线', trigger: 'blur' }
  ]
}

// 获取环境列表
const fetchEnvList = async () => {
  loading.value = true
  try {
    const { data } = await listEnvsWithParams()
    envList.value = data
  } finally {
    loading.value = false
  }
}

// 新增环境
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 编辑环境
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除环境
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除环境"${row.envName}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    await deleteEnv(row.id)
    ElMessage.success('删除成功')
    fetchEnvList()
  })
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      if (!userStore.userInfo) {
        ElMessage.error('获取用户信息失败，请重新登录')
        return
      }
      
      submitting.value = true
      try {
        const data = { ...form.value }
        if (dialogType.value === 'add') {
          await createEnv(data)
          ElMessage.success('新增成功')
        } else {
          await updateEnv(data)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchEnvList()
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error(error.message || '操作失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  form.value = {
    envName: '',
    envCode: '',
    description: '',
  }
}

// 初始化
onMounted(async () => {
  try {
    // 确保获取到用户信息
    if (!userStore.userInfo) {
      await userStore.getUserInfo()
    }
    await fetchEnvList()
  } catch (error) {
    console.error('初始化失败:', error)
    ElMessage.error('获取数据失败')
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  
  .toolbar {
    margin-bottom: 20px;
  }
}
</style> 