import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// En desarrollo, Vite reenvía /api y /auth al WAF del compose para que todo salga del mismo origen (A-25).
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080',
      '/auth': 'http://localhost:8080',
    },
  },
})
