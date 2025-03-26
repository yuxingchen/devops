<template>
  <el-breadcrumb separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span v-if="index === breadcrumbs.length - 1" class="no-redirect">
          {{ item.meta?.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta?.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import {onMounted, ref, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'

const route = useRoute()
const router = useRouter()
const breadcrumbs = ref([])

// 获取面包屑数据
const getBreadcrumb = () => {
  let matched = route.matched.filter(item => item.meta && item.meta.title)
  const first = matched[0]
  
  if (first && first.path !== '/') {
    matched = [{ path: '/', meta: { title: '首页' } }].concat(matched)
  }
  
  breadcrumbs.value = matched
}

// 处理面包屑点击
const handleLink = (item) => {
  router.push(item.path)
}

// 初始化面包屑
onMounted(() => {
  getBreadcrumb()
})

// 监听路由变化
watch(() => route.path, getBreadcrumb)
</script>

<style lang="scss" scoped>
.el-breadcrumb {
  line-height: 1;
  
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
  
  a {
    color: #666;
    cursor: pointer;
    
    &:hover {
      color: #409eff;
    }
  }
}

// 面包屑动画
.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.5s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-active {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-leave-active {
  position: absolute;
}
</style> 