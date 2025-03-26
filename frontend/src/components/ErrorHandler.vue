<template>
  <div v-if="error" class="error-handler">
    <el-alert
      :title="error.title"
      :description="error.message"
      type="error"
      show-icon
      closable
      @close="clearError"
    />
  </div>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from 'vue'
import {errorBus} from '@/utils/error'

const error = ref(null)

// 显示错误
const showError = (err) => {
  error.value = {
    title: err.title || '错误',
    message: err.message || '发生未知错误'
  }
  
  // 5秒后自动清除错误
  setTimeout(() => {
    clearError()
  }, 5000)
}

// 清除错误
const clearError = () => {
  error.value = null
}

// 在组件挂载时添加事件监听
onMounted(() => {
  errorBus.on('error', showError)
})

// 在组件卸载时移除事件监听
onUnmounted(() => {
  errorBus.off('error', showError)
})
</script>

<style lang="scss" scoped>
.error-handler {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  min-width: 380px;
}
</style> 