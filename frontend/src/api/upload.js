import request from '@/utils/request'

// 获取MinIO上传凭证
export function getUploadToken() {
  return request({
    url: '/upload/token',
    method: 'get'
  })
}

// 上传文件
export function uploadFile(data, onUploadProgress) {
  return request({
    url: '/upload/file',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

// 删除文件
export function deleteFile(fileId) {
  return request({
    url: `/upload/delete/${fileId}`,
    method: 'delete'
  })
} 