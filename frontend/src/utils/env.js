// 获取环境变量
export function getEnvValue(key) {
  return import.meta.env[key]
}

// 是否是开发环境
export function isDev() {
  return import.meta.env.VITE_NODE_ENV === 'development'
}

// 是否是生产环境
export function isProd() {
  return import.meta.env.VITE_NODE_ENV === 'production'
}

// 获取API基础路径
export function getApiBaseUrl() {
  return import.meta.env.VITE_API_BASE_URL
}

// 获取上传文件大小限制（MB）
export function getUploadSizeLimit() {
  return import.meta.env.VITE_UPLOAD_SIZE_LIMIT
}

// 是否启用Mock数据
export function useMock() {
  return import.meta.env.VITE_USE_MOCK === 'true'
} 