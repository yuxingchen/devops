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
            @change="handleAppChange"
          >
            <el-option
              v-for="item in appOptions"
              :key="item.id"
              :label="item.appName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部署环境">
          <el-select
            v-model="listQuery.envId"
            placeholder="请选择环境"
            clearable
            filterable
            @change="handleEnvChange"
          >
            <el-option
              v-for="item in envOptions"
              :key="item.id"
              :label="item.envName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="listQuery.keyword"
            placeholder="版本名称/版本号"
            style="width: 200px"
            class="filter-item"
            @keyup.enter.native="handleFilter"
          />
        </el-form-item>
        <el-form-item>
          <el-button class="filter-item" type="primary" @click="handleFilter">
            <el-icon>
              <Search />
            </el-icon>
            搜索
          </el-button>
          <el-button class="filter-item" type="primary" @click="handleUpload">
            <el-icon>
              <Upload />
            </el-icon>
            上传版本
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 版本列表 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column
        label="应用名称"
        prop="appName"
        align="center"
        min-width="180"
      />
      <el-table-column
        label="版本名称"
        prop="versionName"
        align="center"
        min-width="120"
      />
      <el-table-column
        label="版本号"
        prop="versionCode"
        align="center"
        width="120"
      />
      <el-table-column
        label="文件名"
        prop="fileName"
        align="center"
        min-width="200"
      />
      <el-table-column label="文件大小" align="center" width="120">
        <template #default="scope">
          <span>{{ formatFileSize(scope.row.fileSize) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="描述"
        prop="description"
        align="center"
        min-width="200"
      />
      <el-table-column label="创建时间" align="center" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createdTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="320"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="handleDeploy(scope.row, 'all')"
          >
            批量部署
          </el-button>
          <el-button
            type="success"
            size="small"
            @click="handleDeploy(scope.row, 'select')"
          >
            选择部署
          </el-button>
          <el-button type="warning" size="small" @click="handleEdit(scope.row)">
            修改
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.pageNum"
      :limit.sync="listQuery.pageSize"
      @pagination="handlePagination"
    />

    <!-- 上传/修改版本对话框 -->
    <el-dialog
      :title="dialogType === 'create' ? '上传版本' : '修改版本'"
      v-model="uploadDialogVisible"
      width="600px"
    >
      <el-form
        ref="uploadForm"
        :model="uploadForm"
        :rules="uploadRules"
        label-position="left"
        label-width="100px"
        style="width: 500px; margin-left: 50px"
      >
        <el-form-item label="应用" prop="appId">
          <el-select
            v-model="uploadForm.appId"
            placeholder="请选择应用"
            filterable
            :disabled="dialogType === 'edit'"
          >
            <el-option
              v-for="item in appOptions"
              :key="item.id"
              :label="item.appName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="版本名称" prop="versionName">
          <el-input v-model="uploadForm.versionName" />
        </el-form-item>
        <el-form-item label="版本号" prop="versionCode">
          <el-input
            v-model="uploadForm.versionCode"
            :disabled="dialogType === 'edit'"
          />
        </el-form-item>
        <el-form-item label="版本文件" prop="file">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            action="#"
            accept=".jar,.war,.zip"
          >
            <template #trigger>
              <el-button size="small" type="primary">选择文件</el-button>
            </template>
            <template #tip>
              <div class="el-upload__tip">
                当前文件: {{ uploadForm.fileName || "未选择文件" }}
                <br />
                只能上传jar/war/zip文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            :loading="uploadLoading"
            @click="submitUpload"
          >
            {{ uploadLoading ? "上传中..." : "确定" }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 使用新的部署弹窗组件 -->
    <deploy-dialog
      :visible="deployDialogVisible"
      @update:visible="deployDialogVisible = $event"
      :app-id="currentVersion?.appId"
      :app-code="currentVersion?.appCode"
      :version-id="currentVersion?.id"
      :server-options="filteredServerOptions"
      :app-name="currentVersion?.appName"
      :file-name="currentVersion?.fileName"
      :version-name="currentVersion?.versionName"
      :version-code="currentVersion?.versionCode"
      :description="currentVersion?.description"
      :deploy-type="deployType"
      @success="getList"
    />
  </div>
</template>

<script>
import {ElMessage} from "element-plus";
import {deleteVersion, listVersions, updateVersion, uploadVersion,} from "@/api/version";
import {deploy} from "@/api/deploy";
import {listEnvs} from "@/api/host/env";
import {getAppDetail, listApps} from "@/api/app";
import {listServers} from "@/api/host/server";
import Pagination from "@/components/Pagination.vue";
import {formatDateTime} from "@/utils/format";
import DeployDialog from "./components/DeployDialog.vue";
import {useRoute} from "vue-router";

export default {
  name: "VersionList",
  components: { Pagination, DeployDialog },
  data() {
    return {
      appOptions: [],
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        appId: undefined,
        envId: undefined,
        keyword: null,
      },
      listAllParams: {
        pageNum: 1,
        pageSize: 9999
      },
      envOptions: [],
      uploadDialogVisible: false,
      dialogType: "create", // create or edit
      uploadForm: {
        id: null,
        appId: null,
        versionName: "",
        versionCode: "",
        description: "",
        file: null,
        fileName: "",
      },
      uploadRules: {
        appId: [{ required: true, message: "请选择应用", trigger: "change" }],
        versionName: [
          { required: true, message: "版本名称不能为空", trigger: "blur" },
        ],
        versionCode: [
          { required: true, message: "版本号不能为空", trigger: "blur" },
        ],
        file: [
          { required: true, message: "请选择版本文件", trigger: "change" },
        ],
      },
      deployDialogVisible: false,
      deployLoading: false,
      currentVersion: null,
      uploadRef: null,
      uploadLoading: false,
      serverOptions: [], // 服务器选项列表
      deployType: "all", // 部署类型：all-批量部署，select-选择部署
      appServerIds: [], // 当前应用关联的服务器ID列表
    };
  },
  computed: {
    // 根据部署类型过滤服务器选项
    filteredServerOptions() {
      return this.serverOptions.filter((server) =>
        this.appServerIds.includes(server.id)
      );
    },
  },
  created() {
    // 获取路由参数中的应用ID
    const route = useRoute();
    if (route.query.appId) {
      this.listQuery.appId = Number(route.query.appId);
    }

    this.getApps();
    this.getEnvs();
  },
  methods: {
    formatDateTime,
    getApps() {
      listApps(this.listAllParams).then((response) => {
        this.appOptions = response.data.records;
        this.appOptions.forEach((item) => {
          item.appName = `${item.appCode}(${item.appName})`;
        });

        // 如果有默认应用ID，确保该应用在选项中
        if (this.listQuery.appId) {
          const defaultApp = this.appOptions.find(
            (app) => app.id === this.listQuery.appId
          );
          if (!defaultApp) {
            // 如果默认应用不在选项中，清空选择
            this.listQuery.appId = undefined;
          }
        }

        // 获取版本列表
        this.getList();
      });
    },
    handleAppChange() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    getList() {
      this.listLoading = true;
      listVersions(this.listQuery).then((response) => {
        this.list = response.data.records.map((item) => {
          const app = this.appOptions.find((app) => app.id === item.appId);
          return {
            ...item,
            appName: app ? app.appName : "未知应用",
            appCode: app ? app.appCode : "未知应用",
          };
        });
        this.total = response.data.total;
        this.listLoading = false;
      });
    },
    getEnvs() {
      listEnvs().then((response) => {
        this.envOptions = response.data;
      });
    },
    handleEnvChange() {
      this.handleFilter();
    },
    handleFilter() {
      this.listQuery.pageNum = 1;
      this.getList();
    },
    formatFileSize(size) {
      if (!size) return "0 B";
      const units = ["B", "KB", "MB", "GB"];
      let index = 0;
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024;
        index++;
      }
      return size.toFixed(2) + " " + units[index];
    },
    handleEdit(row) {
      this.dialogType = "edit";
      this.uploadForm = {
        id: row.id,
        appId: row.appId,
        versionName: row.versionName,
        versionCode: row.versionCode,
        description: row.description,
        fileName: row.fileName,
        file: null,
      };
      this.uploadDialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.uploadRef) {
          this.$refs.uploadRef.clearFiles();
        }
        this.$refs["uploadForm"].clearValidate();
      });
    },
    handleUpload() {
      this.dialogType = "create";
      this.uploadForm = {
        id: null,
        appId: this.listQuery.appId,
        versionName: "",
        versionCode: "",
        description: "",
        file: null,
        fileName: "",
      };
      this.uploadDialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.uploadRef) {
          this.$refs.uploadRef.clearFiles();
        }
        this.$refs["uploadForm"].clearValidate();
      });
    },
    submitUpload() {
      this.$refs["uploadForm"].validate((valid) => {
        if (valid) {
          this.uploadLoading = true;
          const formData = new FormData();
          if (this.uploadForm.file) {
            formData.append("file", this.uploadForm.file);
          }
          formData.append("appId", this.uploadForm.appId);
          formData.append("versionName", this.uploadForm.versionName);
          formData.append("versionCode", this.uploadForm.versionCode);
          if (this.uploadForm.description) {
            formData.append("description", this.uploadForm.description);
          }
          if (this.dialogType === "edit") {
            formData.append("id", this.uploadForm.id);
          }

          const request =
            this.dialogType === "create"
              ? uploadVersion(formData)
              : updateVersion(formData);
          request
            .then(() => {
              this.uploadDialogVisible = false;
              this.$notify({
                title: "成功",
                message: this.dialogType === "create" ? "上传成功" : "修改成功",
                type: "success",
                duration: 2000,
              });
              this.$refs.uploadRef.clearFiles();
              this.uploadForm.file = null;
              this.uploadForm.fileName = "";
              this.getList();
            })
            .catch(() => {
              // 错误处理已在请求拦截器中统一处理
            })
            .finally(() => {
              this.uploadLoading = false;
            });
        }
      });
    },
    handleFileChange(file) {
      this.uploadForm.file = file.raw;
      this.uploadForm.fileName = file.name;
    },
    handleFileRemove() {
      this.uploadForm.file = null;
      this.uploadForm.fileName = "";
    },
    handleDelete(row) {
      this.$confirm("确认删除该版本吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        deleteVersion(row.id).then(() => {
          this.$notify({
            title: "成功",
            message: "删除成功",
            type: "success",
            duration: 2000,
          });
          this.getList();
        });
      });
    },
    handlePagination({ page, limit }) {
      this.listQuery.pageNum = page;
      this.listQuery.pageSize = limit;
      this.getList();
    },
    handleDeploy(row, type) {
      this.currentVersion = row;
      this.deployType = type;

      // 获取应用详情和服务器列表
      this.getAppServers(row.appId).then(() => {
        this.getServers();
      });
    },
    getServers() {
      listServers()
        .then((response) => {
          this.serverOptions = response.data;
          this.deployDialogVisible = true;
        })
        .catch(() => {
          ElMessage.error("获取服务器列表失败");
        });
    },
    confirmDeploy() {
      this.deployLoading = true;
      deploy({
        appId: this.currentVersion.appId,
        versionId: this.currentVersion.id,
      })
        .then(() => {
          ElMessage({
            type: "success",
            message: "部署任务已创建",
          });
          this.deployDialogVisible = false;
        })
        .finally(() => {
          this.deployLoading = false;
        });
    },
    resetQuery() {
      const appId = this.listQuery.appId;
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        appId: appId,
        envId: undefined,
        keyword: null,
      };
      this.handleFilter();
    },
    // 获取应用关联的服务器列表
    async getAppServers(appId) {
      try {
        const response = await getAppDetail(appId);
        this.appServerIds = response.data.servers
          ? response.data.servers.map(Number)
          : [];
      } catch (error) {
        ElMessage.error("获取应用详情失败");
      }
    },
  },
};
</script>

<style>
.filter-container {
  padding-bottom: 10px;
}

.deploy-info {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.deploy-info p {
  margin: 10px 0;
  line-height: 1.5;
}

.dialog-footer .el-button + .el-button {
  margin-left: 10px;
}
</style>
