<template>
  <div class="app-wrapper">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': !sidebarOpened }">
      <div class="logo">
        <span class="logo-text" v-show="sidebarOpened">DevOps平台</span>
      </div>
      <el-scrollbar>
        <sidebar :sidebar-opened="sidebarOpened" />
      </el-scrollbar>
    </div>
    
    <!-- 主容器 -->
    <div class="main-container">
      <!-- 头部 -->
      <div class="navbar">
        <div class="left">
          <el-icon class="fold-btn" @click="toggleSidebar">
            <Fold v-if="sidebarOpened" />
            <Expand v-else />
          </el-icon>
          <breadcrumb />
        </div>
        <div class="right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="user-info">
              {{ userInfo?.realName || userInfo?.username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 内容区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import {computed, ref} from 'vue'
import {useRouter} from 'vue-router'
import {useUserStore} from '@/store/modules/user'
import {ElMessageBox} from 'element-plus'
import Sidebar from './components/Sidebar.vue'
import Breadcrumb from './components/Breadcrumb.vue'
import {ArrowDown, Expand, Fold} from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const sidebarOpened = ref(true)

const userInfo = computed(() => userStore.userInfo)

const toggleSidebar = () => {
  sidebarOpened.value = !sidebarOpened.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确认退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
}

.sidebar-container {
  width: 210px;
  height: 100%;
  background-color: #304156;
  transition: width 0.3s;
  
  &.is-collapse {
    width: 64px;
  }
  
  .logo {
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #2b2f3a;
    
    .logo-text {
      color: #fff;
      font-size: 16px;
      font-weight: bold;
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 50px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  
  .left {
    display: flex;
    align-items: center;
    
    .fold-btn {
      padding: 0 15px;
      cursor: pointer;
      font-size: 16px;
      
      &:hover {
        background: rgba(0, 0, 0, 0.025);
      }
    }
  }
  
  .right {
    .user-info {
      cursor: pointer;
      color: #666;
      
      .el-icon {
        margin-left: 5px;
      }
    }
  }
}

.app-main {
  flex: 1;
  padding: 15px;
  overflow: auto;
  background: #f0f2f5;
}

// 路由切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style> 