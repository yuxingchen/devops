<template>
  <el-dialog title="部署版本" :model-value="visible" @update:model-value="handleVisibleChange">
    <!-- 版本信息 -->
    <div class="version-info">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="应用名称">{{ appName }}</el-descriptions-item>
        <el-descriptions-item label="文件名称">{{ fileName }}</el-descriptions-item>
        <el-descriptions-item label="版本名称">{{ versionName }}</el-descriptions-item>
        <el-descriptions-item label="版本号">{{ versionCode }}</el-descriptions-item>
        <el-descriptions-item label="版本描述">{{ description }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="margin-top: 20px">
      <!-- 服务器选择 -->
      <el-form-item :label="deployType == 'all' ? '预览服务器' : '部署服务器'" prop="serverIds">
        <el-select v-model="form.serverIds" multiple filterable placeholder="请选择部署服务器" 
          style="width: 100%" @change="handleServerChange">
          <el-option v-for="item in serverOptions" :key="item.id" 
            :label="item.serverName" :value="item.id" />
        </el-select>
      </el-form-item>

      <!-- 备份选项 -->
      <el-form-item label="是否备份" prop="backup">
        <el-switch v-model="form.backup" @change="handleBackupChange" />
        <span class="backup-tip">{{ backupTip }}</span>
      </el-form-item>

      <!-- 备份类型 -->
      <el-form-item label="备份类型" prop="backupType" v-if="form.backup">
        <el-radio-group v-model="form.backupType" :disabled="isJarOrWar" @change="handleBackupChange">
          <el-radio :value="'full'" label="全量备份">全量备份</el-radio>
          <el-radio :value="'incremental'" label="增量备份">增量备份</el-radio>
        </el-radio-group>
      </el-form-item>

      <!-- 部署命令 -->
      <el-form-item label="部署命令" prop="deployCommand">
        <el-input v-model="form.deployCommand" :placeholder="deployCommandPlaceholder" />
      </el-form-item>

      <!-- 命令预览 -->
      <el-form-item label="命令预览">
        <div v-loading="previewLoading" class="preview-container">
          <template v-if="previewCommands.length > 0">
            <div v-for="(server, index) in previewCommands" :key="index" class="server-commands">
              <h4>{{ server.serverName }}执行命令：</h4>
              <div v-for="command in server.commands" :key="command.order" class="command-item">
                <span class="command-purpose">{{ command.order }}.{{ command.purpose }}（{{ command.type }}）：</span>
                <span class="command-content">{{ command.command }}</span>
              </div>
            </div>
          </template>
          <el-empty v-else description="请选择服务器并设置备份选项后查看命令预览" />
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" :loading="deployLoading" @click="handleDeploy">确认部署</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import {computed, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {deploy, previewDeployCommands} from "@/api/deploy";

const props = defineProps({
  visible: { type: Boolean, required: true },
  appId: { type: Number, required: true },
  versionId: { type: Number, required: true },
  serverOptions: { type: Array, required: true },
  appName: { type: String, required: true },
  fileName: { type: String, required: true },
  versionName: { type: String, required: true },
  versionCode: { type: String, required: true },
  description: { type: String, default: "-" },
  appCode: { type: String, required: true },
  deployType: { type: String, required: true }
});

const emit = defineEmits(["update:visible", "success"]);
const formRef = ref(null);
const deployLoading = ref(false);
const previewLoading = ref(false);
const previewCommands = ref([]);

const form = ref({
  serverIds: [],
  backup: true,
  backupType: 'full',
  deployCommand: ""
});

const rules = {
  serverIds: [{ required: true, message: "请选择部署服务器", trigger: "change" }],
  backup: [{ required: true, message: "请选择是否备份", trigger: "change" }]
};

// 计算属性
const backupTip = computed(() => {
  const fileName = props.fileName || "";
  if (isJarOrWar.value) return "建议：JAR/WAR包建议开启备份";
  if (fileName.endsWith(".zip")) return "建议：ZIP包一般无需备份。如需备份，请在部署命令前用&&追加备份命令";
  return "";
});

const deployCommandPlaceholder = computed(() => {
  const fileName = props.fileName || "";
  const appCode = props.appCode || "";
  if (isJarOrWar.value) return `请输入部署命令，默认为：sh startup_${appCode}.sh`;
  if (fileName.endsWith(".zip")) return `请输入部署命令，默认为：unzip -o ${fileName}`;
  return "请输入部署命令";
});

const isJarOrWar = computed(() => {
  const fileName = props.fileName || '';
  return fileName.endsWith('.jar') || fileName.endsWith('.war');
});

// 监听和处理函数
watch(() => props.visible, (val) => {
  if (val) {
    form.value.backup = isJarOrWar.value;
    form.value.backupType = isJarOrWar.value ? 'full' : 'incremental';
    form.value.serverIds = [];
    previewCommands.value = [];
  }
});

const handleVisibleChange = (val) => emit("update:visible", val);
const handleCancel = () => emit("update:visible", false);

const handleServerChange = async () => {
  if (form.value.serverIds.length === 0) {
    previewCommands.value = [];
    return;
  }

  try {
    previewLoading.value = true;
    const { data } = await previewDeployCommands({
      appId: props.appId,
      versionId: props.versionId,
      serverIds: form.value.serverIds.join(","),
      backup: form.value.backup,
      backupType: form.value.backupType,
      deployCommand: form.value.deployCommand
    });
    previewCommands.value = data;
  } catch (error) {
    console.error("获取预览命令失败:", error);
  } finally {
    previewLoading.value = false;
  }
};

const handleBackupChange = () => {
  if (form.value.serverIds.length > 0) handleServerChange();
};

const handleDeploy = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        deployLoading.value = true;
        await deploy({
          appId: props.appId,
          versionId: props.versionId,
          serverIds: props.deployType === "all" ? "" : form.value.serverIds.join(","),
          backup: form.value.backup,
          backupType: form.value.backupType,
          deployCommand: form.value.deployCommand
        });
        ElMessage.success("部署任务已提交");
        emit("success");
        emit("update:visible", false);
      } catch (error) {
        console.error("部署失败:", error);
      } finally {
        deployLoading.value = false;
      }
    }
  });
};
</script>

<style lang="scss" scoped>
.version-info {
  margin-bottom: 20px;
  :deep(.el-descriptions) {
    padding: 12px;
    background-color: var(--el-fill-color-light);
    border-radius: 4px;
  }
}

.preview-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
  min-height: 100px;
  max-height: 400px;
  overflow-y: auto;
}

.server-commands {
  margin-bottom: 20px;
  h4 {
    margin: 0 0 10px;
    color: #409eff;
  }
}

.command-item {
  margin-bottom: 10px;
  .command-purpose {
    font-size: 13px;
    color: #666;
    margin-bottom: 5px;
  }
  .command-content {
    background: #f5f7fa;
    padding: 8px;
    border-radius: 4px;
    font-family: monospace;
    word-break: break-all;
  }
}

.backup-tip {
  margin-left: 10px;
  color: #909399;
  font-size: 13px;
}
</style>
