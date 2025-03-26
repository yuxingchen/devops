<template>
  <div class="app-container">
    <div class="toolbar">
      <el-form :inline="true">
        <el-form-item label="部署环境">
          <el-select
              v-model="query.envId"
              placeholder="请选择环境"
              clearable
              filterable
              @change="handleEnvChange"
          >
            <el-option
                v-for="item in envList"
                :key="item.id"
                :label="item.envName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务器名称">
          <el-input
              v-model="query.serverName"
              placeholder="请输入服务器名称"
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="服务器IP">
          <el-input
              v-model="query.serverIp"
              placeholder="请输入服务器IP"
              @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search/>
            </el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon>
              <Refresh/>
            </el-icon>
            重置
          </el-button>
          <el-button type="primary" @click="handleAdd">
            <el-icon>
              <Plus/>
            </el-icon>
            新增服务器
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table v-loading="loading" :data="serverList" border style="width: 100%">
      <el-table-column prop="serverName" label="服务器名称"/>
      <el-table-column prop="serverIp" label="服务器IP"/>
      <el-table-column prop="serverPort" label="SSH端口"/>
      <el-table-column prop="username" label="登录用户"/>
      <el-table-column prop="userPath" label="用户路径" show-overflow-tooltip/>
      <el-table-column label="认证方式" width="100">
        <template #default="{ row }">
          {{ row.privateKey ? '密钥' : '密码' }}
        </template>
      </el-table-column>
      <el-table-column prop="createdTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createdTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="success" size="small" @click="handleTest(row)">
            测试连接
          </el-button>
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
        <el-form-item label="部署环境" prop="envId">
          <el-select
              v-model="form.envId"
              placeholder="请选择环境"
              :disabled="dialogType === 'edit'"
          >
            <el-option
                v-for="item in envList"
                :key="item.id"
                :label="item.envName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务器名称" prop="serverName">
          <el-input v-model="form.serverName" placeholder="请输入服务器名称"/>
        </el-form-item>
        <el-form-item label="服务器IP" prop="serverIp">
          <el-input v-model="form.serverIp" placeholder="请输入服务器IP"/>
        </el-form-item>
        <el-form-item label="SSH端口" prop="serverPort">
          <el-input-number v-model="form.serverPort" :min="1" :max="65535"/>
        </el-form-item>
        <el-form-item label="登录用户" prop="username">
          <el-input v-model="form.username" placeholder="请输入登录用户名"/>
        </el-form-item>
        <el-form-item label="认证方式">
          <el-radio-group v-model="form.authType">
            <el-radio label="password">密码认证</el-radio>
            <el-radio label="key">密钥认证</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
            v-if="form.authType === 'password'"
            label="登录密码"
            prop="password"
        >
          <el-input
              v-model="form.password"
              type="password"
              rows="2"
              placeholder="请输入登录密码"
              show-password
          />
        </el-form-item>
        <el-form-item
            v-else
            label="私钥内容"
            prop="privateKey"
        >
          <el-input
              v-model="form.privateKey"
              type="textarea"
              rows="5"
              placeholder="请输入私钥内容"
          />
        </el-form-item>
        <el-form-item label="用户路径" prop="userPath">
          <el-input v-model="form.userPath" placeholder="请输入用户路径"/>
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
import {computed, onMounted, ref, watch} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {listEnvs} from '@/api/host/env'
import {createServer, deleteServer, listServersWithParams, testConnection, updateServer} from '@/api/host/server'
import {formatDateTime} from '@/utils/format'
import {encryptBase64} from "@/utils/crypto";

// 查询条件
const query = ref({
  envId: null,
  serverName: '',
  serverIp: ''
})

// 环境列表
const envList = ref([])
// 服务器列表
const serverList = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add')
const dialogTitle = computed(() => dialogType.value === 'add' ? '新增服务器' : '编辑服务器')
const formRef = ref(null)
const submitting = ref(false)

// 表单数据
const form = ref({
  envId: null,
  serverName: '',
  serverIp: '',
  serverPort: 22,
  username: '',
  authType: 'password',
  password: '',
  privateKey: '',
  userPath: ''
})

// 表单校验规则
const rules = {
  envId: [
    {required: true, message: '请选择部署环境', trigger: 'change'}
  ],
  serverName: [
    {required: true, message: '请输入服务器名称', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  serverIp: [
    {required: true, message: '请输入服务器IP', trigger: 'blur'},
    {pattern: /^(\d{1,3}\.){3}\d{1,3}$/, message: '请输入正确的IP地址', trigger: 'blur'}
  ],
  serverPort: [
    {required: true, message: '请输入SSH端口', trigger: 'blur'}
  ],
  username: [
    {required: true, message: '请输入登录用户名', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入登录密码', trigger: 'blur'}
  ],
  privateKey: [
    {required: true, message: '请输入私钥内容', trigger: 'blur'}
  ],
  userPath: [
    {required: true, message: '请输入用户路径', trigger: 'blur'}
  ]
}

// 获取环境列表
const fetchEnvList = async () => {
  try {
    const {data} = await listEnvs()
    envList.value = data
  } catch (error) {
    console.error('获取环境列表失败:', error)
  }
}

// 获取服务器列表
const fetchServerList = async () => {
  loading.value = true
  try {
    const {data} = await listServersWithParams(query.value)
    serverList.value = data
  } finally {
    loading.value = false
  }
}

// 新增服务器
const handleAdd = () => {
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 编辑服务器
const handleEdit = (row) => {
  dialogType.value = 'edit'
  form.value = {
    ...row,
    authType: row.privateKey ? 'key' : 'password'
  }
  dialogVisible.value = true
}

// 删除服务器
const handleDelete = (row) => {
  ElMessageBox.confirm(
      `确定要删除服务器"${row.serverName}"吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  ).then(async () => {
    await deleteServer(row.id)
    ElMessage.success('删除成功')
    fetchServerList()
  })
}

// 测试连接
const handleTest = async (row) => {
  try {
    await testConnection(row.id)
    ElMessage.success('连接成功')
  } catch (error) {
    console.error('测试连接失败:', error)
  }
}

// 提交表单
const handleSubmit = () => {
  formRef.value?.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const data = {...form.value}
        // 根据认证方式清除不需要的字段
        if (data.authType === 'password') {
          delete data.privateKey
          data.password = encryptBase64(data.password)
        } else {
          delete data.password
        }
        delete data.authType

        if (dialogType.value === 'add') {
          await createServer(data)
          ElMessage.success('新增成功')
        } else {
          await updateServer(data.id, data)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
        fetchServerList()
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
    envId: null,
    serverName: '',
    serverIp: '',
    serverPort: 22,
    username: '',
    authType: 'password',
    password: '',
    privateKey: '',
    userPath: ''
  }
}

// 查询
const handleSearch = () => {
  fetchServerList()
}

// 重置
const handleReset = () => {
  query.value = {
    envId: null,
    serverName: '',
    serverIp: ''
  }
  fetchServerList()
}

// 监听查询条件变化
watch(() => query.value.envId, () => {
  fetchServerList()
})

// 初始化
onMounted(() => {
  fetchEnvList()
  fetchServerList()
})
</script>