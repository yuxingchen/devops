import {defineStore} from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebarOpened: true,
    loading: {
      global: false,
      text: '加载中...'
    }
  }),
  
  actions: {
    toggleSidebar() {
      this.sidebarOpened = !this.sidebarOpened
    },
    
    setLoading(isLoading, text = '加载中...') {
      this.loading.global = isLoading
      this.loading.text = text
    }
  }
})