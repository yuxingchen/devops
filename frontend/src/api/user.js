import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/api/users/login',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: '/api/users/info',
    method: 'get'
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/users/logout',
    method: 'post'
  })
}

// 获取用户列表
export function getUserList() {
  return request({
    url: '/api/users',
    method: 'get'
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: '/api/users',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: `/api/users/${data.id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 更改用户状态
export function changeUserStatus(id) {
  return request({
    url: `/api/users/${id}/status`,
    method: 'put'
  })
}

// 获取用户列表
export function listUsers(params) {
  return request({
    url: '/api/users',
    method: 'get',
    params
  })
}

// 更新用户信息
export function updateUserInfo(data) {
  return request({
    url: '/api/users/info',
    method: 'put',
    data
  })
}

// 修改密码
export function updatePassword(data) {
  return request({
    url: '/api/users/password',
    method: 'put',
    data
  })
} 