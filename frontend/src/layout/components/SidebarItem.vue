<template>
  <div v-if="!item.hidden">
    <!-- 无子菜单 -->
    <template v-if="!hasChildren(item)">
      <el-menu-item :index="resolvePath(basePath)" @click="handleClick(resolvePath(basePath))">
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>
          <span>{{ item.meta?.title }}</span>
        </template>
      </el-menu-item>
    </template>
    
    <!-- 有子菜单 -->
    <el-sub-menu v-else :index="resolvePath(basePath)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      
      <sidebar-item
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'

const router = useRouter()
const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  basePath: {
    type: String,
    default: ''
  }
})

// 判断是否有子菜单
const hasChildren = (route) => {
  return route.children && route.children.length > 0 && !route.children.every(item => item.hidden)
}

// 解析路径
const resolvePath = (routePath) => {
  if (routePath.startsWith('/')) {
    return routePath
  }
  return props.basePath + '/' + routePath
}

// 处理菜单点击
const handleClick = (path) => {
  router.push(path)
}
</script> 