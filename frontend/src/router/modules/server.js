export default {
  path: '/server',
  component: () => import('@/layout/index.vue'),
  name: 'Server',
  meta: {
    title: '服务器管理',
    icon: 'server'
  },
  children: [
    {
      path: 'list',
      component: () => import('@/views/server/list/index.vue'),
      name: 'ServerList',
      meta: {
        title: '服务器列表'
      }
    },
    {
      path: 'cluster',
      component: () => import('@/views/server/cluster/index.vue'),
      name: 'ServerCluster',
      meta: {
        title: '集群管理'
      }
    }
  ]
}; 