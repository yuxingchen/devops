<template>
  <el-menu
    :default-active="activeMenu"
    :collapse="!sidebarOpened"
    :unique-opened="true"
    :collapse-transition="false"
    background-color="#304156"
    text-color="#bfcbd9"
    active-text-color="#409eff"
  >
    <sidebar-item
      v-for="route in routes"
      :key="route.path"
      :item="route"
      :base-path="route.path"
    />
  </el-menu>
</template>

<script setup>
import {computed} from 'vue'
import {useRoute} from 'vue-router'
import {routes} from '@/router'
import SidebarItem from './SidebarItem.vue'

const route = useRoute()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 从父组件接收侧边栏状态
const props = defineProps({
  sidebarOpened: {
    type: Boolean,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.el-menu {
  border: none;
  height: 100%;
  width: 100%;
}
</style> 