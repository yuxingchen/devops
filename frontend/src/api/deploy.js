import request from '@/utils/request'

// 预览部署命令
export function previewDeployCommands(data) {
  return request({
    url: '/api/deploy/preview-commands',
    method: 'post',
    data
  })
}

// 执行部署
export function deploy(data) {
  return request({
    url: '/api/deploy',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: data
  })
}

// 获取部署记录列表
export function listRecords(params) {
  return request({
    url: '/api/deploy/records',
    method: 'get',
    params
  })
}

// 获取部署日志
export function getDeployLogs(recordId) {
  return request({
    url: `/api/deploy/records/${recordId}/logs`,
    method: 'get'
  })
}

// 回滚部署
export function rollback(recordId) {
  return request({
    url: `/api/deploy/${recordId}/rollback`,
    method: 'post'
  })
} 