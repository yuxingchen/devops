<template>
  <div id="app">
    <error-handler />
    <global-loading />
    <router-view />
  </div>
</template>

<script setup>
import ErrorHandler from '@/components/ErrorHandler.vue'
import GlobalLoading from '@/components/GlobalLoading.vue'
</script>

<style lang="scss">
@import '@/assets/styles/index.scss';
</style> 