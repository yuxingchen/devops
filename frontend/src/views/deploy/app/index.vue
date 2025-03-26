<template>
  <div class="app-container">
    <!-- 搜索工具栏 -->
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item>
          <el-input v-model="listQuery.keyword" placeholder="应用名称/编码" style="width: 200px;" class="filter-item"
            @keyup.enter.native="handleFilter" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate">
            <el-icon>
              <Plus />
            </el-icon>
            新建应用
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 应用列表 -->
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%;">
      <el-table-column label="应用编码" prop="appCode" align="center" />
      <el-table-column label="应用名称" prop="appName" align="center" />
      <el-table-column label="应用端口" prop="appPort" align="center" />
      <el-table-column label="部署路径" prop="deployPath" align="center" />
      <el-table-column label="备份路径" prop="backupPath" align="center" />
      <el-table-column label="描述" prop="description" align="center" />
      <el-table-column label="创建时间" align="center">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createdTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="250">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button type="success" size="small" @click="handleVersion(scope.row)">
            版本管理
          </el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize"
      @pagination="handlePagination" />

    <!-- 应用表单对话框 -->
    <el-dialog :title="textMap[dialogStatus]" v-model="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px"
        style=" padding: 10px">
        <el-form-item label="应用编码" prop="appCode">
          <el-input v-model="temp.appCode" :disabled="dialogStatus === 'update'" placeholder="请输入应用编码" />
        </el-form-item>
        <el-form-item label="应用名称" prop="appName">
          <el-input v-model="temp.appName" placeholder="请输入应用名称" />
        </el-form-item>
        <el-form-item label="应用端口" prop="appPort">
          <el-input-number v-model="temp.appPort" :min="1" :max="65535" placeholder="请输入应用端口"
            controls-position="right" />
        </el-form-item>
        <el-form-item label="关联服务器" prop="servers">
          <el-select v-model="temp.servers" multiple filterable placeholder="请选择服务器" style="width: 100%">
            <el-option v-for="item in serverOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联集群" prop="clusterIds">
          <el-select v-model="temp.clusterIds" multiple filterable placeholder="请选择集群" style="width: 100%">
            <el-option v-for="item in clusterOptions" :key="item.id" :label="item.clusterName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="部署路径" prop="deployPath">
          <el-input v-model="temp.deployPath" placeholder="请输入部署路径，最终为{服务器用户路径}/{部署路径}" />
        </el-form-item>
        <el-form-item label="备份路径" prop="backupPath">
          <el-input v-model="temp.backupPath" placeholder="请输入备份路径，默认为{服务器用户路径}/backup/{应用编码}" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="temp.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { createApp, deleteApp, listApps, updateApp } from '@/api/app'
import { listServers } from '@/api/host/server'
import { pageServerClusters } from '@/api/host/serverCluster'
import Pagination from '@/components/Pagination.vue'
import { formatDateTime } from "@/utils/format";

export default {
  name: 'AppList',
  components: { Pagination },
  data() {
    // 自定义校验规则：根据部署方式校验关联对象
    const validateDeployTarget = (rule, value, callback) => {
      if (this.temp.servers && this.temp.servers.length === 0 && this.temp.clusterIds && this.temp.clusterIds.length === 0) {
        callback(new Error('请选择关联服务器或集群'));
      } else {
        callback();
      }
    };

    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        keyword: undefined
      },
      serverOptions: [],
      clusterOptions: [],
      temp: {
        id: undefined,
        appCode: '',
        appName: '',
        appPort: undefined,
        deployPath: '',
        backupPath: '',
        description: '',
        servers: [],
        clusterIds: []
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑应用',
        create: '新建应用'
      },
      rules: {
        appCode: [{ required: true, message: '应用编码不能为空', trigger: 'blur' }],
        appName: [{ required: true, message: '应用名称不能为空', trigger: 'blur' }],
        appPort: [{ required: true, message: '应用端口不能为空', trigger: 'blur' }],
        deployPath: [{ required: true, message: '部署路径不能为空', trigger: 'blur' }],
        servers: [{ validator: validateDeployTarget, trigger: 'change' }],
        clusterIds: [{ validator: validateDeployTarget, trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
    this.getServers()
    this.getClusters()
  },
  methods: {
    formatDateTime,
    getList() {
      this.listLoading = true
      listApps(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getServers() {
      listServers().then(response => {
        this.serverOptions = response.data.map(item => ({
          id: item.id,
          name: `${item.serverName}(${item.serverIp})`
        }))
      })
    },
    getClusters() {
      pageServerClusters({ current: 1, size: 1000 }).then(response => {
        this.clusterOptions = response.data.records
      })
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        appCode: '',
        appName: '',
        appPort: undefined,
        deployPath: '',
        backupPath: '',
        description: '',
        servers: [],
        clusterIds: []
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row)
      // 处理服务器和集群ID
      if (row.serverIds) {
        this.temp.servers = row.serverIds.split(',').map(Number)
      }
      if (row.serverClusterIds) {
        this.temp.clusterIds = row.serverClusterIds.split(',').map(Number)
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          tempData.backupPath = tempData.backupPath || `/backup/${tempData.appCode}`
          // 处理服务器和集群ID
          tempData.serverIds = tempData.servers?.length > 0 ? tempData.servers.join(',') : ''
          tempData.serverClusterIds = tempData.clusterIds?.length > 0 ? tempData.clusterIds.join(',') : ''
          createApp(tempData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '创建成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          // 处理服务器和集群ID
          tempData.serverIds = tempData.servers?.length > 0 ? tempData.servers.join(',') : ''
          tempData.serverClusterIds = tempData.clusterIds?.length > 0 ? tempData.clusterIds.join(',') : ''
          updateApp(tempData.id, tempData).then(() => {
            this.dialogFormVisible = false
            this.$notify({
              title: '成功',
              message: '更新成功',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除该应用吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteApp(row.id).then(() => {
          this.$notify({
            title: '成功',
            message: '删除成功',
            type: 'success',
            duration: 2000
          })
          this.getList()
        })
      })
    },
    handleVersion(row) {
      this.$router.push({ path: '/deploy/version', query: { appId: row.id } })
    },
    handlePagination({ page, limit }) {
      this.listQuery.pageNum = page
      this.listQuery.pageSize = limit
      this.getList()
    },
  }
}
</script>