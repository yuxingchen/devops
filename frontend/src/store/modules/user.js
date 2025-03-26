import {defineStore} from 'pinia'
import {getUserInfo, login} from '@/api/user'
import {getToken, removeToken, setToken} from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    userInfo: null
  }),
  
  getters: {
    // 获取用户ID
    userId: (state) => state.userInfo?.id
  },
  
  actions: {
    // 登录
    async login(loginData) {
      try {
        const { data } = await login(loginData)
        this.token = data
        setToken(data)
        return data
      } catch (error) {
        throw error
      }
    },
    
    // 获取用户信息
    async getUserInfo() {
      try {
        const { data } = await getUserInfo()
        this.userInfo = data
        // 存储用户ID到localStorage
        if (data?.id) {
          localStorage.setItem('userId', data.id)
        }
        return data
      } catch (error) {
        throw error
      }
    },
    
    // 退出登录
    logout() {
      return new Promise((resolve) => {
        removeToken()
        localStorage.removeItem('userId')
        this.token = ''
        this.userInfo = null
        resolve()
      })
    },
  }
}) 