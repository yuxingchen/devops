import request from '@/utils/request'

// 获取版本列表
export function getVersionList() {
  return request({
    url: '/api/versions',
    method: 'get'
  })
}

// 获取版本详情
export function getVersionDetail(id) {
  return request({
    url: `/api/versions/${id}`,
    method: 'get'
  })
}

// 上传版本
export function uploadVersion(data) {
  return request({
    url: '/api/versions/upload',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

// 更新版本
export function updateVersion(data) {
  return request({
    url: '/api/versions/update',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data
  })
}

// 删除版本
export function deleteVersion(id) {
  return request({
    url: `/api/versions/${id}`,
    method: 'delete'
  })
}

// 获取版本列表
export function listVersions(params) {
  return request({
    url: '/api/versions',
    method: 'get',
    params
  })
}
