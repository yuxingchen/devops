import {createRouter, createWebHashHistory} from 'vue-router'
import Layout from '@/layout/index.vue'
import {getToken} from '@/utils/auth'

export const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/deploy/app',
    hidden: true
  },
  {
    path: '/deploy',
    component: Layout,
    redirect: '/deploy/app',
    name: 'Deploy',
    meta: { title: '部署管理', icon: 'Tools' },
    children: [
      {
        path: 'app',
        component: () => import('@/views/deploy/app/index.vue'),
        name: 'App',
        meta: { title: '应用管理', icon: 'Platform' }
      },
      {
        path: 'version',
        component: () => import('@/views/deploy/version/index.vue'),
        name: 'Version',
        meta: { title: '版本管理', icon: 'Files' }
      },
      {
        path: 'record',
        component: () => import('@/views/deploy/record/index.vue'),
        name: 'Record',
        meta: { title: '部署记录', icon: 'List' }
      }
    ]
  },
  {
    path: '/host',
    component: Layout,
    redirect: '/host/env',
    name: 'Host',
    meta: { title: '主机管理', icon: 'Monitor' },
    children: [
      {
        path: 'env',
        component: () => import('@/views/host/env/index.vue'),
        name: 'Env',
        meta: { title: '环境管理', icon: 'SetUp' }
      },
      {
        path: 'server',
        component: () => import('@/views/host/server/index.vue'),
        name: 'Server',
        meta: { title: '服务器管理', icon: 'Monitor' }
      },
      {
        path: 'cluster',
        component: () => import('@/views/host/cluster/index.vue'),
        name: 'ServerCluster',
        meta: { title: '集群管理', icon: 'Connection' }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    name: 'System',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人信息' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (to.path === '/login') {
    next()
  } else {
    if (!token) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router 