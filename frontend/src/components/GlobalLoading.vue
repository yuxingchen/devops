<template>
  <div v-if="loading" class="global-loading">
    <div class="loading-mask">
      <el-icon class="loading-icon"><Loading /></el-icon>
      <p class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {useAppStore} from '@/store/modules/app'

const appStore = useAppStore()
const loading = computed(() => appStore.loading.global)
const text = computed(() => appStore.loading.text)
</script>

<style lang="scss" scoped>
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  
  .loading-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    
    .loading-icon {
      font-size: 40px;
      color: #409eff;
      animation: rotating 2s linear infinite;
    }
    
    .loading-text {
      margin-top: 10px;
      color: #606266;
      font-size: 14px;
    }
  }
}

@keyframes rotating {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 