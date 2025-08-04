<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { onMounted, onUnmounted } from 'vue'
import nativeWebSocketService from '../services/nativeWebSocketService'

export default {
  name: 'App',
  setup() {
    onMounted(async () => {
      console.log('🚀 App.vue - Uygulama başlatılıyor, WebSocket bağlantısı kuruluyor...')
      
      try {
        // Global WebSocket bağlantısını başlat
        await nativeWebSocketService.connect()
        
        // Bağlantı durumu değişikliklerini dinle
        nativeWebSocketService.onConnectionChange((connected) => {
          if (connected) {
            console.log('✅ App.vue - Global WebSocket bağlantısı aktif')
          } else {
            console.log('❌ App.vue - Global WebSocket bağlantısı kesildi')
          }
        })
        
        console.log('✅ App.vue - WebSocket bağlantısı başarıyla kuruldu')
        
      } catch (error) {
        console.error('❌ App.vue - WebSocket bağlantı hatası:', error)
      }
    })
    
    onUnmounted(() => {
      console.log('🚪 App.vue - Uygulama kapatılıyor, WebSocket temizleniyor')
      // WebSocket bağlantısını kapatma, sadece log
    })
  }
}
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0;
  padding: 0;
  height: 100vh;
  overflow: hidden;
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  background-color: #f8fafc;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

html {
  height: 100%;
}

/* Scrollbar styling */
::-webkit-scrollbar {
  width: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Element Plus customizations */
.el-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.el-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.el-button {
  border-radius: 8px;
  font-weight: 500;
}

.el-input__wrapper {
  border-radius: 8px;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f8fafc;
  font-weight: 600;
}

.el-table td {
  padding: 12px 0;
}

/* Responsive table */
@media (max-width: 768px) {
  .el-table {
    font-size: 12px;
  }
  
  .el-table th,
  .el-table td {
    padding: 8px 4px;
  }
}
</style> 