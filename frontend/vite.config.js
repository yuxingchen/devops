import {defineConfig, loadEnv} from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig(({mode}) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd())
  
  return {
    plugins: [vue()],
    base: env.VITE_API_BASE_URL || '/',
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    },
    server: {
      port: 3000,
      proxy: {
        '/api': {
          target: 'http://localhost:8080',
          changeOrigin: true
        }
      }
    },
    define: {
      'process.env': env
    },
    build: {
      outDir: 'dist',
      assetsDir: 'assets',
      rollupOptions: {
        output: {
          format: 'esm'  // 指定输出格式为ESM
        }
      }
    }
  }
})