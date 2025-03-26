<template>
  <div class="app-container">
    <!-- 搜索工具栏 -->
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item label="应用">
          <el-select
              v-model="listQuery.appId"
              placeholder="请选择应用"
              clearable
              filterable
              @change="handleFilter"
          >
            <el-option
                v-for="item in appOptions"
                :key="item.id"
                :label="item.appName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务器">
          <el-select
              v-model="listQuery.serverId"
              placeholder="请选择服务器"
              clearable
              filterable
              @change="handleFilter"
          >
            <el-option
                v-for="item in serverOptions"
                :key="item.id"
                :label="item.serverName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
    </div>

    <!-- 部署记录列表 -->
    <el-table
        v-loading="listLoading"
        :data="list"
        border
        fit
        highlight-current-row
        style="width: 100%;"
    >
      <el-table-column label="应用名称" prop="appName" align="center" width="250"/>
      <el-table-column label="服务器" align="center">
        <template #default="scope">
          <el-tooltip
              v-if="scope.row.serverNames"
              :content="scope.row.serverNames"
              placement="top"
          >
            <span>{{ scope.row.serverNames }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center">
        <template #default="scope">
          <span>{{ scope.row.versionName ? `${scope.row.versionName}(${scope.row.versionCode})` : '未知版本' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="部署状态" align="center" width="100">
        <template #default="scope">
          <el-tag
              :type="scope.row.deployStatus === 1 ? 'success' : scope.row.deployStatus === 2 ? 'danger' : 'warning'">
            {{ scope.row.deployStatus === 1 ? '成功' : scope.row.deployStatus === 2 ? '失败' : '进行中' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" prop="startTime" align="center">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" prop="endTime" align="center" >
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" prop="createdByName" align="center"/>
      <el-table-column label="操作" align="center">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleViewLog(scope.row)">
            查看日志
          </el-button>
          <el-button
              v-if="scope.row.deployStatus === 1"
              type="warning"
              size="small"
              @click="handleRollback(scope.row)"
          >
            回滚
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-show="total>0"
        :total="total"
        :page.sync="listQuery.pageNum"
        :limit.sync="listQuery.pageSize"
        @pagination="handlePagination"
    />

    <!-- 部署日志对话框 -->
    <el-dialog
        title="部署日志"
        v-model="logDialogVisible"
        :fullscreen="false"
        :close-on-click-modal="false"
        :close-on-press-escape="true"
        class="deploy-log-dialog"
    >
      <div class="log-content">
        <div v-for="(log, index) in deployLogs" :key="index" class="log-item">
          <span class="log-time">{{ formatDateTime(log.createdTime) }}</span>
          <span :class="['log-content', { 'error': log.logType === 1 }]">{{ log.logContent }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="logDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import {ElMessage, ElMessageBox} from 'element-plus'
import {getDeployLogs, listRecords, rollback} from '@/api/deploy'
import {listApps} from '@/api/app'
import {listServers} from '@/api/host/server'
import Pagination from '@/components/Pagination.vue'
import {formatDateTime} from "@/utils/format"

export default {
  name: 'DeployRecordList',
  components: {Pagination},
  data() {
    return {
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        appId: null,
        serverId: null
      },
      appOptions: [],
      serverOptions: [],
      logDialogVisible: false,
      deployLogs: [],
      currentRecord: null
    }
  },
  created() {
    this.getApps()
    this.getServers()
    this.getList()
  },
  methods: {
    formatDateTime,
    getList() {
      this.listLoading = true
      listRecords(this.listQuery).then(response => {
        this.list = response.data.records
        this.total = response.data.total
        this.listLoading = false
      })
    },
    getApps() {
      listApps().then(response => {
        this.appOptions = response.data.records
        this.appOptions.forEach(item => {
          item.appName = `${item.appCode}(${item.appName})`
        })
      })
    },
    getServers() {
      listServers().then(response => {
        this.serverOptions = response.data
        this.serverOptions.forEach(item => {
          item.serverName = `${item.serverName}(${item.serverIp})`
        })
      })
    },
    handleFilter() {
      this.listQuery.pageNum = 1
      this.getList()
    },
    handleViewLog(row) {
      this.currentRecord = row
      this.logDialogVisible = true
      getDeployLogs(row.id).then(response => {
        this.deployLogs = response.data
      })
    },
    handleRollback(row) {
      ElMessageBox.confirm('确认要回滚到该版本吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        rollback(row.id).then(() => {
          ElMessage({
            type: 'success',
            message: '回滚成功'
          })
          this.getList()
        })
      })
    },
    handlePagination({page, limit}) {
      this.listQuery.pageNum = page
      this.listQuery.pageSize = limit
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
.deploy-log-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
    max-height: calc(90vh - 120px);
  }
}

.log-content {
  overflow-y: auto;
  padding:  0 10px  0 10px;
  background-color: #1e1e1e;
  color: #fff;
  font-family: Monaco, Menlo, Consolas, "Courier New", monospace;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.log-item {
  margin: 10px 0 5px;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
}

.log-time {
  color: #888;
  margin-right: 10px;
  flex-shrink: 0;
  min-width: 150px;
}

.log-content {
  flex: 1;
  
  &.error {
    color: #ff4949;
  }
}
</style>