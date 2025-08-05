<template>
  <div class="native-websocket-test">
    <h1>Native WebSocket Test</h1>
    
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
      <el-button @click="sendFlightUpdate" :disabled="!isConnected" type="warning">
        Flight Update Gönder
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

const setupMessageHandlers = () => {
  nativeWebSocketService.onFlightUpdate((data) => {
    addMessage(`Flight update: ${JSON.stringify(data)}`)
    addLog(`Flight update alındı: ${JSON.stringify(data)}`)
  })
  
  nativeWebSocketService.onMessage('CONNECTION_ESTABLISHED', (data) => {
    addMessage(`Bağlantı kuruldu: ${data}`)
    addLog('CONNECTION_ESTABLISHED mesajı alındı')
  })
  
  nativeWebSocketService.onMessage('FLIGHT_UPDATE_CONFIRMED', (data) => {
    addMessage(`Flight update onaylandı: ${data}`)
    addLog(`FLIGHT_UPDATE_CONFIRMED alındı: ${data}`)
  })
  
  nativeWebSocketService.onMessage('ERROR', (data) => {
    addMessage(`Hata: ${data}`)
    addLog(`ERROR mesajı alındı: ${data}`)
  })
  
  nativeWebSocketService.onMessage('PONG', (data) => {
    addMessage(`Pong alındı: ${data}`)
    addLog(`PONG mesajı alındı: ${data}`)
  })
  
  nativeWebSocketService.onMessage('FLIGHT_UPDATE', (data) => {
    addMessage(`FLIGHT_UPDATE: ${JSON.stringify(data)}`)
    addLog(`FLIGHT_UPDATE mesajı alındı: ${JSON.stringify(data)}`)
  })
}

const disconnect = () => {
  nativeWebSocketService.disconnect()
  isConnected.value = false
  connectionStatus.value = 'Disconnected'
  addLog('Native WebSocket bağlantısı kesildi')
  ElMessage.info('Native WebSocket bağlantısı kesildi')
}

const sendTestMessage = () => {
  nativeWebSocketService.send('PING', 'ping')
  addLog('Ping mesajı gönderildi')
  ElMessage.success('Ping mesajı gönderildi')
}

const sendFlightUpdate = () => {
  const testFlight = {
    id: 1,
    flightNumber: 'TK123',
    origin: 'IST',
    destination: 'JFK',
    status: 'ON_TIME'
  }
  
  nativeWebSocketService.sendFlightUpdate('CREATE', testFlight)
  addLog('Flight update mesajı gönderildi')
  ElMessage.success('Flight update mesajı gönderildi')
}

nativeWebSocketService.onConnectionChange((connected) => {
  isConnected.value = connected
  connectionStatus.value = connected ? 'Connected' : 'Disconnected'
  addLog(`Bağlantı durumu değişti: ${connected ? 'Bağlandı' : 'Kesildi'}`)
})

onMounted(() => {
  addLog('Native WebSocket test sayfası yüklendi')
  
  setupMessageHandlers()
})
</script>

<style scoped>
.native-websocket-test {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.status-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.messages, .logs {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  max-height: 300px;
  overflow-y: auto;
}

.message, .log {
  padding: 5px 0;
  border-bottom: 1px solid #eee;
}

.message:last-child, .log:last-child {
  border-bottom: none;
}

.Connected {
  color: #28a745;
  font-weight: bold;
}

.Disconnected {
  color: #dc3545;
  font-weight: bold;
}
</style> 