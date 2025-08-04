<template>
  <div class="websocket-test">
    <h1>WebSocket Test</h1>
    
    <div class="status-section">
      <h3>Bağlantı Durumu</h3>
      <p>Status: <span :class="connectionStatus">{{ connectionStatus }}</span></p>
      <p>Reconnect Attempts: {{ reconnectAttempts }}</p>
    </div>

    <div class="controls">
      <el-button @click="connect" :disabled="isConnected" type="primary">
        Bağlan
      </el-button>
      <el-button @click="disconnect" :disabled="!isConnected" type="danger">
        Bağlantıyı Kes
      </el-button>
      <el-button @click="sendTestMessage" :disabled="!isConnected" type="success">
        Test Mesajı Gönder
      </el-button>
      <el-button @click="sendPing" :disabled="!isConnected" type="warning">
        Ping Gönder
      </el-button>
    </div>

    <div class="messages">
      <h3>Gelen Mesajlar</h3>
      <div v-for="(message, index) in messages" :key="index" class="message">
        <strong>{{ message.timestamp }}</strong>: {{ message.content }}
      </div>
    </div>

    <div class="logs">
      <h3>Log'lar</h3>
      <div v-for="(log, index) in logs" :key="index" class="log">
        {{ log }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import nativeWebSocketService from '@/services/nativeWebSocketService'

const isConnected = ref(false)
const connectionStatus = ref('Disconnected')
const reconnectAttempts = ref(0)
const messages = ref([])
const logs = ref([])

const addLog = (message) => {
  logs.value.push(`${new Date().toLocaleTimeString()}: ${message}`)
}

const addMessage = (content) => {
  messages.value.push({
    timestamp: new Date().toLocaleTimeString(),
    content
  })
}

const connect = async () => {
  try {
    addLog('Native WebSocket bağlantısı başlatılıyor...')
    
    // Bağlantı durumu dinleyicisi
    nativeWebSocketService.onConnectionChange((connected) => {
      isConnected.value = connected
      connectionStatus.value = connected ? 'Connected' : 'Disconnected'
      addLog(`Bağlantı durumu değişti: ${connected ? 'Bağlı' : 'Bağlı değil'}`)
    })
    
    // Mesaj dinleyicileri
    nativeWebSocketService.onMessage('CONNECTION_ESTABLISHED', (message) => {
      addMessage(`Bağlantı kuruldu: ${JSON.stringify(message.data)}`)
    })
    
    nativeWebSocketService.onMessage('PONG', (message) => {
      addMessage(`Pong alındı: ${JSON.stringify(message.data)}`)
    })
    
    nativeWebSocketService.onMessage('FLIGHT_UPDATE', (message) => {
      addMessage(`Flight update: ${JSON.stringify(message.data)}`)
    })
    
    nativeWebSocketService.onMessage('FLIGHT_UPDATE_CONFIRMED', (message) => {
      addMessage(`Flight update onaylandı: ${JSON.stringify(message.data)}`)
    })
    
    nativeWebSocketService.onMessage('ERROR', (message) => {
      addMessage(`Hata: ${JSON.stringify(message.data)}`)
    })
    
    // Genel mesaj dinleyicisi
    nativeWebSocketService.onMessage('*', (message) => {
      addMessage(`Genel mesaj [${message.type}]: ${JSON.stringify(message.data)}`)
    })
    
    await nativeWebSocketService.connect()
    
    isConnected.value = true
    connectionStatus.value = 'Connected'
    addLog('Native WebSocket bağlantısı başarılı!')
    
    ElMessage.success('Native WebSocket bağlantısı başarılı!')
  } catch (error) {
    addLog(`Native WebSocket bağlantı hatası: ${error.message}`)
    ElMessage.error('Native WebSocket bağlantısı başarısız!')
  }
}

const disconnect = () => {
  nativeWebSocketService.disconnect()
  isConnected.value = false
  connectionStatus.value = 'Disconnected'
  addLog('Native WebSocket bağlantısı kesildi')
  ElMessage.info('Native WebSocket bağlantısı kesildi')
}

const sendTestMessage = () => {
  const testMessage = {
    action: 'TEST',
    message: 'Bu bir test mesajıdır',
    timestamp: new Date().toISOString()
  }
  
  nativeWebSocketService.send('FLIGHT_UPDATE', testMessage)
  addLog('Test mesajı gönderildi')
  ElMessage.success('Test mesajı gönderildi')
}

const sendPing = () => {
  nativeWebSocketService.ping()
  addLog('Ping mesajı gönderildi')
  ElMessage.info('Ping mesajı gönderildi')
}

onMounted(() => {
  addLog('Native WebSocket test sayfası yüklendi')
})

onUnmounted(() => {
  nativeWebSocketService.disconnect()
})
</script>

<style scoped>
.websocket-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.status-section {
  background: #f5f5f5;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.controls {
  margin-bottom: 20px;
}

.controls .el-button {
  margin-right: 10px;
}

.messages, .logs {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.message, .log {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.Connected {
  color: green;
  font-weight: bold;
}

.Disconnected {
  color: red;
  font-weight: bold;
}
</style> 