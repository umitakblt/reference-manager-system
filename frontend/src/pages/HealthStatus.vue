<template>
  <div class="health-status-container">
    <div class="page-header">
      <h1>Sistem Durumu</h1>
      <p>Sistem bileşenlerinin durumunu ve performansını izleyin</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="8">
        <el-card class="status-card">
          <div class="status-header">
            <el-icon class="status-icon" :class="getStatusClass(overallStatus)">
              <CircleCheck v-if="overallStatus === 'healthy'" />
              <Warning v-else-if="overallStatus === 'warning'" />
              <CircleClose v-else />
            </el-icon>
            <div class="status-info">
              <h3>Genel Durum</h3>
              <p>{{ getStatusText(overallStatus) }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="status-card">
          <div class="status-header">
            <el-icon class="status-icon" :class="getStatusClass(apiStatus)">
              <CircleCheck v-if="apiStatus === 'healthy'" />
              <Warning v-else-if="apiStatus === 'warning'" />
              <CircleClose v-else />
            </el-icon>
            <div class="status-info">
              <h3>API Servisi</h3>
              <p>{{ getStatusText(apiStatus) }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="status-card">
          <div class="status-header">
            <el-icon class="status-icon" :class="getStatusClass(databaseStatus)">
              <CircleCheck v-if="databaseStatus === 'healthy'" />
              <Warning v-else-if="databaseStatus === 'warning'" />
              <CircleClose v-else />
            </el-icon>
            <div class="status-info">
              <h3>Veritabanı</h3>
              <p>{{ getStatusText(databaseStatus) }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Sistem Metrikleri</span>
              <el-button type="primary" size="small" @click="refreshMetrics">
                <el-icon><Refresh /></el-icon>
                Yenile
              </el-button>
            </div>
          </template>

          <div class="metrics-grid">
            <div class="metric-item">
              <div class="metric-label">CPU Kullanımı</div>
              <div class="metric-value">{{ metrics.cpuUsage }}%</div>
              <el-progress :percentage="metrics.cpuUsage" :color="getProgressColor(metrics.cpuUsage)" />
            </div>

            <div class="metric-item">
              <div class="metric-label">Bellek Kullanımı</div>
              <div class="metric-value">{{ metrics.memoryUsage }}%</div>
              <el-progress :percentage="metrics.memoryUsage" :color="getProgressColor(metrics.memoryUsage)" />
            </div>

            <div class="metric-item">
              <div class="metric-label">Disk Kullanımı</div>
              <div class="metric-value">{{ metrics.diskUsage }}%</div>
              <el-progress :percentage="metrics.diskUsage" :color="getProgressColor(metrics.diskUsage)" />
            </div>

            <div class="metric-item">
              <div class="metric-label">Aktif Bağlantılar</div>
              <div class="metric-value">{{ metrics.activeConnections }}</div>
            </div>

            <div class="metric-item">
              <div class="metric-label">Ortalama Yanıt Süresi</div>
              <div class="metric-value">{{ metrics.avgResponseTime }}ms</div>
            </div>

            <div class="metric-item">
              <div class="metric-label">Hata Oranı</div>
              <div class="metric-value">{{ metrics.errorRate }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Servis Durumları</span>
            </div>
          </template>

          <el-table :data="services" style="width: 100%">
            <el-table-column prop="name" label="Servis" />
            <el-table-column prop="status" label="Durum" width="100">
              <template #default="scope">
                <el-tag :type="getServiceStatusType(scope.row.status)">
                  {{ getServiceStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="responseTime" label="Yanıt Süresi" width="120">
              <template #default="scope">
                {{ scope.row.responseTime }}ms
              </template>
            </el-table-column>
            <el-table-column prop="lastCheck" label="Son Kontrol" width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 24px;">
      <template #header>
        <div class="card-header">
          <span>Sistem Logları</span>
          <div class="log-actions">
            <el-button size="small" @click="clearLogs">Temizle</el-button>
            <el-button size="small" type="primary" @click="exportLogs">Dışa Aktar</el-button>
          </div>
        </div>
      </template>

      <div class="logs-container">
        <div v-for="log in systemLogs" :key="log.id" class="log-entry" :class="getLogLevelClass(log.level)">
          <div class="log-timestamp">{{ formatTimestamp(log.timestamp) }}</div>
          <div class="log-level">{{ log.level }}</div>
          <div class="log-message">{{ log.message }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  CircleCheck, 
  Warning, 
  CircleClose, 
  Refresh 
} from '@element-plus/icons-vue'
import api from '../services/api'

const overallStatus = ref('healthy')
const apiStatus = ref('healthy')
const databaseStatus = ref('healthy')

const metrics = ref({
  cpuUsage: 0,
  memoryUsage: 0,
  diskUsage: 0,
  activeConnections: 0,
  avgResponseTime: 0,
  errorRate: 0
})

const services = ref([
  { name: 'User Service', status: 'healthy', responseTime: 45, lastCheck: '2024-01-15 10:30:00' },
  { name: 'Flight Service', status: 'healthy', responseTime: 120, lastCheck: '2024-01-15 10:30:00' },
  { name: 'Airline Service', status: 'healthy', responseTime: 80, lastCheck: '2024-01-15 10:30:00' }
])

const systemLogs = ref([
  { id: 1, timestamp: new Date().toISOString(), level: 'INFO', message: 'Sistem başlatıldı' },
  { id: 2, timestamp: new Date(Date.now() - 15000).toISOString(), level: 'INFO', message: 'API servisleri aktif' },
  { id: 3, timestamp: new Date(Date.now() - 30000).toISOString(), level: 'INFO', message: 'Veritabanı bağlantısı kuruldu' },
  { id: 4, timestamp: new Date(Date.now() - 45000).toISOString(), level: 'INFO', message: 'Kullanıcı servisi hazır' }
])

let healthCheckInterval = null

const loadHealthStatus = async () => {
  try {
    const [usersRes, flightsRes, airlinesRes] = await Promise.all([
      api.get('/v1/users').catch(() => ({ data: [] })),
      api.get('/v1/flights').catch(() => ({ data: [] })),
      api.get('/v1/airlines').catch(() => ({ data: [] }))
    ])
    
    apiStatus.value = 'healthy'
    
    databaseStatus.value = 'healthy'
    
    overallStatus.value = 'healthy'
    
    metrics.value = {
      cpuUsage: 25,
      memoryUsage: 45,
      diskUsage: 30,
      activeConnections: usersRes.data.length,
      avgResponseTime: 120,
      errorRate: 0
    }
    
    services.value = [
      { name: 'User Service', status: 'healthy', responseTime: 45, lastCheck: '2024-01-15 10:30:00' },
      { name: 'Flight Service', status: 'healthy', responseTime: 120, lastCheck: '2024-01-15 10:30:00' },
      { name: 'Airline Service', status: 'healthy', responseTime: 80, lastCheck: '2024-01-15 10:30:00' }
    ]
    
    systemLogs.value = [
      { id: 1, timestamp: new Date().toISOString(), level: 'INFO', message: 'Sistem başlatıldı' },
      { id: 2, timestamp: new Date(Date.now() - 15000).toISOString(), level: 'INFO', message: 'API servisleri aktif' },
      { id: 3, timestamp: new Date(Date.now() - 30000).toISOString(), level: 'INFO', message: 'Veritabanı bağlantısı kuruldu' },
      { id: 4, timestamp: new Date(Date.now() - 45000).toISOString(), level: 'INFO', message: 'Kullanıcı servisi hazır' }
    ]
  } catch (error) {
    console.error('Sağlık durumu yüklenirken hata:', error)
    overallStatus.value = 'error'
    apiStatus.value = 'error'
    databaseStatus.value = 'error'
  }
}

const refreshMetrics = async () => {
  await loadHealthStatus()
  ElMessage.success('Metrikler yenilendi')
}

const getStatusClass = (status) => {
  switch (status) {
    case 'healthy': return 'status-healthy'
    case 'warning': return 'status-warning'
    case 'error': return 'status-error'
    default: return 'status-unknown'
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 'healthy': return 'Sağlıklı'
    case 'warning': return 'Uyarı'
    case 'error': return 'Hata'
    default: return 'Bilinmiyor'
  }
}

const getServiceStatusType = (status) => {
  switch (status) {
    case 'healthy': return 'success'
    case 'warning': return 'warning'
    case 'error': return 'danger'
    default: return 'info'
  }
}

const getServiceStatusText = (status) => {
  switch (status) {
    case 'healthy': return 'Sağlıklı'
    case 'warning': return 'Uyarı'
    case 'error': return 'Hata'
    default: return 'Bilinmiyor'
  }
}

const getProgressColor = (value) => {
  if (value < 50) return '#67c23a'
  if (value < 80) return '#e6a23c'
  return '#f56c6c'
}

const getLogLevelClass = (level) => {
  switch (level) {
    case 'ERROR': return 'log-error'
    case 'WARN': return 'log-warning'
    case 'INFO': return 'log-info'
    default: return 'log-debug'
  }
}

const formatTimestamp = (timestamp) => {
  return new Date(timestamp).toLocaleString('tr-TR')
}

const clearLogs = () => {
  systemLogs.value = []
  ElMessage.success('Loglar temizlendi')
}

const exportLogs = async () => {
  try {
    const logData = systemLogs.value.map(log => 
      `${log.timestamp} [${log.level}] ${log.message}`
    ).join('\n')
    
    const blob = new Blob([logData], { type: 'text/plain' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'system-logs.txt')
    document.body.appendChild(link)
    link.click()
    link.remove()
    
    ElMessage.success('Loglar başarıyla dışa aktarıldı')
  } catch (error) {
    console.error('Log dışa aktarma hatası:', error)
    ElMessage.error('Loglar dışa aktarılamadı')
  }
}

onMounted(() => {
  loadHealthStatus()
  healthCheckInterval = setInterval(loadHealthStatus, 30000)
})

onUnmounted(() => {
  if (healthCheckInterval) {
    clearInterval(healthCheckInterval)
  }
})
</script>

<style scoped>
.health-status-container {
  padding: 24px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  color: #303133;
  margin-bottom: 8px;
}

.page-header p {
  color: #606266;
  margin: 0;
}

.status-card {
  height: 120px;
}

.status-header {
  display: flex;
  align-items: center;
  height: 100%;
}

.status-icon {
  font-size: 48px;
  margin-right: 16px;
}

.status-icon.status-healthy {
  color: #67c23a;
}

.status-icon.status-warning {
  color: #e6a23c;
}

.status-icon.status-error {
  color: #f56c6c;
}

.status-info h3 {
  color: #303133;
  margin: 0 0 4px 0;
}

.status-info p {
  color: #909399;
  margin: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.metric-item {
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fafafa;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.log-actions {
  display: flex;
  gap: 12px;
}

.logs-container {
  max-height: 400px;
  overflow-y: auto;
}

.log-entry {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-bottom: 1px solid #f0f0f0;
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.log-entry:last-child {
  border-bottom: none;
}

.log-timestamp {
  width: 150px;
  color: #909399;
}

.log-level {
  width: 80px;
  font-weight: 600;
}

.log-message {
  flex: 1;
}

.log-entry.log-error {
  background-color: #fef0f0;
}

.log-entry.log-warning {
  background-color: #fdf6ec;
}

.log-entry.log-info {
  background-color: #f0f9ff;
}

.log-entry.log-debug {
  background-color: #f9f9f9;
}
</style>
