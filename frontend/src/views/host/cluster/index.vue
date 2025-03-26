<template>
  <div class="app-container">
    <!-- 搜索工具栏 -->
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="集群名称">
          <el-input
            v-model="listQuery.clusterName"
            placeholder="请输入集群名称"
            style="width: 200px"
            class="filter-item"
            @keyup.enter="handleFilter"
          />
        </el-form-item>
        <el-form-item label="集群编码">
          <el-input
            v-model="listQuery.clusterCode"
            placeholder="请输入集群编码"
            style="width: 200px"
            class="filter-item"
            @keyup.enter="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleFilter">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增集群
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 集群列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column label="集群名称" prop="clusterName" align="center" />
      <el-table-column label="集群编码" prop="clusterCode" align="center" />
      <el-table-column label="集群IP" prop="clusterIp" align="center" />
      <el-table-column label="描述" prop="description" align="center" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createdTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page="listQuery.current"
      :limit="listQuery.size"
      @pagination="getList"
    />

    <!-- 集群表单对话框 -->
    <server-cluster-form
      v-model="dialogVisible"
      :edit-data="currentCluster"
      @success="getList"
    />
  </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {deleteServerCluster, pageServerClusters} from '@/api/host/serverCluster'
import {formatDateTime} from '@/utils/format'
import Pagination from '@/components/Pagination.vue'
import ServerClusterForm from '@/components/ServerClusterForm.vue'

// 列表数据
const list = ref([])
const total = ref(0)
const listLoading = ref(true)
const listQuery = reactive({
  current: 1,
  size: 10,
  clusterName: '',
  clusterCode: ''
})

// 对话框数据
const dialogVisible = ref(false)
const currentCluster = ref(null)

// 获取列表数据
const getList = async () => {
  try {
    listLoading.value = true
    const res = await pageServerClusters(listQuery)
    list.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取集群列表失败:', error)
  } finally {
    listLoading.value = false
  }
}

// 查询
const handleFilter = () => {
  listQuery.current = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  listQuery.clusterName = ''
  listQuery.clusterCode = ''
  handleFilter()
}

// 新增集群
const handleCreate = () => {
  currentCluster.value = null
  dialogVisible.value = true
}

// 编辑集群
const handleUpdate = (row) => {
  currentCluster.value = row
  dialogVisible.value = true
}

// 删除集群
const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该集群吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteServerCluster(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除集群失败:', error)
    }
  })
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.filter-container {
  padding-bottom: 10px;
}

.filter-item {
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 10px;
}
</style> 