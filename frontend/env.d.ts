/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_NODE_ENV: string
  readonly VITE_API_BASE_URL: string
  readonly VITE_UPLOAD_SIZE_LIMIT: number
  readonly VITE_USE_MOCK: boolean
}

interface ImportMeta {
  readonly env: ImportMetaEnv
} 