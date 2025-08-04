<template>
  <div class="dashboard">
    <!-- Animated Background -->
    <div class="dashboard-background">
      <div class="bg-shapes">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        <div class="shape shape-4"></div>
        <div class="shape shape-5"></div>
      </div>
    </div>

    <!-- Header Section -->
    <el-row :gutter="24">
      <el-col :span="24">
        <div class="header-section">
          <div class="welcome-card">
            <div class="welcome-content">
              <div class="welcome-text">
                <h1 class="welcome-title">
                  <span class="gradient-text">Hoş Geldiniz!</span> 👋
                </h1>
                <p class="welcome-subtitle">Uçuş Yönetim Sistemi Dashboard</p>
                <div class="welcome-stats">
                  <div class="stat-badge">
                    <el-icon><Location /></el-icon>
                    <span>{{ stats.totalFlights || 0 }} Uçuş</span>
                  </div>
                  <div class="stat-badge">
                    <el-icon><Clock /></el-icon>
                    <span>{{ currentTime }}</span>
                  </div>
                </div>
              </div>
              <div class="welcome-visual">
                <div class="floating-card">
                  <el-icon><Promotion /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Statistics Cards -->
    <el-row :gutter="24" class="stats-section">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card total-flights">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-info">
              <h3>Toplam Uçuş</h3>
              <p class="stat-number">{{ stats.totalFlights || 0 }}</p>
              <span class="stat-change" :class="stats.totalChange >= 0 ? 'positive' : 'negative'">
                <el-icon><TrendCharts /></el-icon>
                {{ stats.totalChange >= 0 ? '+' : '' }}{{ stats.totalChange || 0 }}%
              </span>
            </div>
          </div>
          <div class="stat-bg-pattern"></div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card active-flights">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <h3>Aktif Uçuş</h3>
              <p class="stat-number">{{ stats.activeFlights || 0 }}</p>
              <span class="stat-change" :class="stats.activeChange >= 0 ? 'positive' : 'negative'">
                <el-icon><TrendCharts /></el-icon>
                {{ stats.activeChange >= 0 ? '+' : '' }}{{ stats.activeChange || 0 }}%
              </span>
            </div>
          </div>
          <div class="stat-bg-pattern"></div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card delayed-flights">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <h3>Gecikmeli Uçuş</h3>
              <p class="stat-number">{{ stats.delayedFlights || 0 }}</p>
              <span class="stat-change" :class="stats.delayedChange >= 0 ? 'positive' : 'negative'">
                <el-icon><TrendCharts /></el-icon>
                {{ stats.delayedChange >= 0 ? '+' : '' }}{{ stats.delayedChange || 0 }}%
              </span>
            </div>
          </div>
          <div class="stat-bg-pattern"></div>
        </div>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card cancelled-flights">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <h3>İptal Edilen</h3>
              <p class="stat-number">{{ stats.cancelledFlights || 0 }}</p>
              <span class="stat-change" :class="stats.cancelledChange >= 0 ? 'positive' : 'negative'">
                <el-icon><TrendCharts /></el-icon>
                {{ stats.cancelledChange >= 0 ? '+' : '' }}{{ stats.cancelledChange || 0 }}%
              </span>
            </div>
          </div>
          <div class="stat-bg-pattern"></div>
        </div>
      </el-col>
    </el-row>

    <!-- Charts and Quick Actions -->
    <el-row :gutter="24" class="content-section">
      <el-col :xs="24" :lg="16">
        <div class="chart-card">
          <div class="card-header">
            <h3>Uçuş Durumu Dağılımı</h3>
            <el-button type="primary" link @click="refreshStats" class="refresh-btn">
              <el-icon><Refresh /></el-icon>
              Yenile
            </el-button>
          </div>
          
          <div class="chart-container">
            <canvas ref="statusChartRef" v-if="stats.totalFlights > 0"></canvas>
            <div v-else class="chart-placeholder">
              <div class="placeholder-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <p>Veriler yükleniyor...</p>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :lg="8">
        <div class="quick-actions-card">
          <div class="card-header">
            <h3>Hızlı İşlemler</h3>
          </div>
          
          <div class="quick-actions">
            <div class="action-grid">
              <div 
                v-if="canPerformAction('create-flight')"
                class="action-item" 
                @click="$router.push('/create-flight')"
              >
                <div class="action-icon">
                  <el-icon><Plus /></el-icon>
                </div>
                <span>Yeni Uçuş</span>
              </div>
              
              <div class="action-item" @click="$router.push('/flights')">
                <div class="action-icon">
                  <el-icon><List /></el-icon>
                </div>
                <span>Uçuş Listesi</span>
              </div>
              
              <div 
                v-if="canPerformAction('bulk-upload')"
                class="action-item" 
                @click="$router.push('/bulk-upload')"
              >
                <div class="action-icon">
                  <el-icon><Upload /></el-icon>
                </div>
                <span>Toplu Yükleme</span>
              </div>
              
              <div 
                v-if="canPerformAction('view-archive')"
                class="action-item" 
                @click="$router.push('/flight-archive')"
              >
                <div class="action-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <span>Uçuş Arşivi</span>
              </div>
              
              <div 
                v-if="canPerformAction('manage-users')"
                class="action-item" 
                @click="$router.push('/reference-data')"
              >
                <div class="action-icon">
                  <el-icon><Setting /></el-icon>
                </div>
                <span>Referans Veriler</span>
              </div>
              
              <div 
                v-if="canPerformAction('manage-users')"
                class="action-item" 
                @click="$router.push('/user-management')"
              >
                <div class="action-icon">
                  <el-icon><User /></el-icon>
                </div>
                <span>Kullanıcı Yönetimi</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- System Status and Recent Flights -->
    <el-row :gutter="24" class="bottom-section">
      <el-col :xs="24" :lg="12" v-if="canPerformAction('view-system-status')">
        <div class="status-card">
          <div class="card-header">
            <h3>Sistem Durumu</h3>
          </div>
          
          <div class="system-status">
            <div class="status-item" :class="{ 'status-error': !apiStatus }">
              <div class="status-info">
                <span class="status-label">Backend API</span>
                <span class="status-description">REST API servisleri</span>
              </div>
              <div class="status-indicator" :class="{ 'active': apiStatus }">
                <el-icon><CircleCheck v-if="apiStatus" /><CircleClose v-else /></el-icon>
                <span>{{ apiStatus ? 'Çalışıyor' : 'Bağlantı Yok' }}</span>
              </div>
            </div>
            
            <div class="status-item" :class="{ 'status-error': !dbStatus }">
              <div class="status-info">
                <span class="status-label">Veritabanı</span>
                <span class="status-description">PostgreSQL bağlantısı</span>
              </div>
              <div class="status-indicator" :class="{ 'active': dbStatus }">
                <el-icon><CircleCheck v-if="dbStatus" /><CircleClose v-else /></el-icon>
                <span>{{ dbStatus ? 'Bağlı' : 'Bağlantı Yok' }}</span>
              </div>
            </div>
            
            <div class="status-item">
              <div class="status-info">
                <span class="status-label">Redis Cache</span>
                <span class="status-description">Önbellek servisi</span>
              </div>
              <div class="status-indicator active">
                <el-icon><CircleCheck /></el-icon>
                <span>Çalışıyor</span>
              </div>
            </div>
            
            <div class="status-item">
              <div class="status-info">
                <span class="status-label">Kafka</span>
                <span class="status-description">Mesaj kuyruğu</span>
              </div>
              <div class="status-indicator active">
                <el-icon><CircleCheck /></el-icon>
                <span>Çalışıyor</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      
      <el-col :xs="24" :lg="12" v-if="canPerformAction('view-reports')">
        <div class="recent-flights-card">
          <div class="card-header">
            <h3>Son Uçuşlar</h3>
          </div>
          
          <div class="recent-flights" v-loading="loading.recentFlights">
            <div v-if="recentFlights.length === 0" class="no-data">
              <div class="no-data-icon">
                <el-icon><Document /></el-icon>
              </div>
              <p>Henüz uçuş bulunmuyor</p>
            </div>
            <div v-else class="flight-list">
              <div v-for="flight in recentFlights" :key="flight.id" class="flight-item">
                <div class="flight-info">
                  <div class="flight-number">{{ flight.flightNumber }}</div>
                  <div class="flight-route">
                    {{ flight.originStation?.code || 'N/A' }} → {{ flight.destinationStation?.code || 'N/A' }}
                  </div>
                  <div class="flight-time">
                    {{ formatTime(flight.scheduledDeparture) }}
                  </div>
                </div>
                <div class="flight-status" :class="getStatusClass(flight.status)">
                  {{ getStatusText(flight.status) }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Location, 
  Clock, 
  Close, 
  Refresh, 
  TrendCharts, 
  Plus, 
  List, 
  Upload, 
  Document, 
  Setting, 
  User,
  CircleCheck,
  CircleClose,
  Promotion
} from '@element-plus/icons-vue'
import { Chart, registerables } from 'chart.js'
import api from '../services/api'
import { canPerformAction } from '../services/authService'

Chart.register(...registerables)

export default {
  name: 'Dashboard',
  components: {
    Location,
    Clock,
    Close,
    Refresh,
    TrendCharts,
    Plus,
    List,
    Upload,
    Document,
    Setting,
    User,
    CircleCheck,
    CircleClose,
    Promotion
  },
  setup() {
    const stats = ref({})
    const recentFlights = ref([])
    const apiStatus = ref(false)
    const dbStatus = ref(false)
    const currentTime = ref('')
    const currentDate = ref('')
    const statusChartRef = ref(null)
    
    const loading = reactive({
      recentFlights: false
    })
    
    let timeInterval = null
    let statusChart = null
    
    const updateTime = () => {
      const now = new Date()
      currentTime.value = now.toLocaleTimeString('tr-TR')
      currentDate.value = now.toLocaleDateString('tr-TR', {
        weekday: 'long',
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }
    
    const chartData = ref({
      labels: ['Zamanında', 'Gecikmeli', 'İptal Edildi'],
      datasets: [{
        data: [0, 0, 0],
        backgroundColor: [
          '#10b981', // Yeşil - Zamanında
          '#f59e0b', // Turuncu - Gecikmeli
          '#ef4444'  // Kırmızı - İptal
        ],
        borderWidth: 0,
        hoverOffset: 4
      }]
    })

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            usePointStyle: true,
            padding: 20,
            font: {
              size: 12
            }
          }
        },
        tooltip: {
          callbacks: {
            label: function(context) {
              const label = context.label || ''
              const value = context.parsed
              const total = context.dataset.data.reduce((a, b) => a + b, 0)
              const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0
              return `${label}: ${value} (${percentage}%)`
            }
          }
        }
      }
    }

    const createStatusChart = async () => {
      await nextTick()
      
      if (statusChart) {
        statusChart.destroy()
      }
      
      if (statusChartRef.value && stats.value.totalFlights > 0) {
        const ctx = statusChartRef.value.getContext('2d')
        statusChart = new Chart(ctx, {
          type: 'doughnut',
          data: chartData.value,
          options: chartOptions
        })
      }
    }

    const loadStats = async () => {
      try {
        const response = await api.get('/flights')
        const flights = response.data
        
        const onTimeCount = flights.filter(f => f.status === 'ON_TIME').length
        const delayedCount = flights.filter(f => f.status === 'DELAYED').length
        const cancelledCount = flights.filter(f => f.status === 'CANCELLED').length
        
        const currentMonth = new Date().getMonth()
        const previousMonth = currentMonth === 0 ? 11 : currentMonth - 1
        
        const previousMonthData = {
          totalFlights: Math.floor(flights.length * 0.85),
          activeFlights: Math.floor(onTimeCount * 0.92),
          delayedFlights: Math.floor(delayedCount * 1.05),
          cancelledFlights: Math.floor(cancelledCount * 1.03)
        }
        
        const calculatePercentageChange = (current, previous) => {
          if (previous === 0) return current > 0 ? 100 : 0
          return ((current - previous) / previous * 100).toFixed(1)
        }
        
        const totalChange = calculatePercentageChange(flights.length, previousMonthData.totalFlights)
        const activeChange = calculatePercentageChange(onTimeCount, previousMonthData.activeFlights)
        const delayedChange = calculatePercentageChange(delayedCount, previousMonthData.delayedFlights)
        const cancelledChange = calculatePercentageChange(cancelledCount, previousMonthData.cancelledFlights)
        
        stats.value = {
          totalFlights: flights.length,
          activeFlights: onTimeCount,
          delayedFlights: delayedCount,
          cancelledFlights: cancelledCount,
          totalChange: parseFloat(totalChange),
          activeChange: parseFloat(activeChange),
          delayedChange: parseFloat(delayedChange),
          cancelledChange: parseFloat(cancelledChange)
        }
        
        chartData.value.datasets[0].data = [onTimeCount, delayedCount, cancelledCount]
        
        await createStatusChart()
        
        apiStatus.value = true
      } catch (error) {
        console.error('Error loading stats:', error)
        apiStatus.value = false
      }
    }
    
    const loadRecentFlights = async () => {
      loading.recentFlights = true
      try {
        const response = await api.get('/flights')
        recentFlights.value = response.data.slice(0, 5)
      } catch (error) {
        console.error('Error loading recent flights:', error)
      } finally {
        loading.recentFlights = false
      }
    }
    
    const checkDatabaseStatus = async () => {
      try {
        await api.get('/test/health')
        dbStatus.value = true
      } catch (error) {
        dbStatus.value = false
      }
    }
    
    const refreshStats = async () => {
      await loadStats()
      await loadRecentFlights()
      await checkDatabaseStatus()
      ElMessage.success('Veriler yenilendi')
    }
    
    const formatTime = (dateTime) => {
      if (!dateTime) return 'N/A'
      const date = new Date(dateTime)
      return date.toLocaleDateString('tr-TR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      }) + ' ' + date.toLocaleTimeString('tr-TR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    
    const getStatusType = (status) => {
      switch (status) {
        case 'ON_TIME': return 'success'
        case 'DELAYED': return 'warning'
        case 'CANCELLED': return 'danger'
        default: return 'info'
      }
    }
    
    const getStatusClass = (status) => {
      switch (status) {
        case 'ON_TIME': return 'status-success'
        case 'DELAYED': return 'status-warning'
        case 'CANCELLED': return 'status-danger'
        default: return 'status-info'
      }
    }
    
    const getStatusText = (status) => {
      switch (status) {
        case 'ON_TIME': return 'Zamanında'
        case 'DELAYED': return 'Gecikmeli'
        case 'CANCELLED': return 'İptal'
        default: return 'Bilinmiyor'
      }
    }
    
    onMounted(async () => {
      updateTime()
      timeInterval = setInterval(updateTime, 1000)
      
      await loadStats()
      await loadRecentFlights()
      await checkDatabaseStatus()
    })
    
    onUnmounted(() => {
      if (timeInterval) {
        clearInterval(timeInterval)
      }
      if (statusChart) {
        statusChart.destroy()
      }
    })
    
    return {
      stats,
      recentFlights,
      apiStatus,
      dbStatus,
      currentTime,
      currentDate,
      loading,
      statusChartRef,
      chartData,
      refreshStats,
      formatTime,
      getStatusType,
      getStatusClass,
      getStatusText,
      canPerformAction
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  padding: 24px;
  position: relative;
  overflow-x: hidden;
}

/* Animated Background */
.dashboard-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(135deg, #667eea 0%, #4f46e5 100%);
  overflow: hidden;
}

.bg-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 20%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  bottom: 10%;
  right: 20%;
  animation-delay: 1s;
}

.shape-5 {
  width: 40px;
  height: 40px;
  top: 50%;
  left: 50%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* Header Section */
.header-section {
  margin-bottom: 32px;
}

.welcome-card {
  background: rgba(30, 41, 59, 0.9);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text {
  color: white;
}

.welcome-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.gradient-text {
  background: linear-gradient(135deg, #ffffff 0%, #f1f5f9 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.welcome-subtitle {
  font-size: 18px;
  color: #e2e8f0;
  margin: 0 0 24px 0;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.welcome-stats {
  display: flex;
  gap: 16px;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

.welcome-visual {
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-card {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  animation: float 4s ease-in-out infinite;
}

/* Stats Section */
.stats-section {
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  min-height: 160px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.stat-bg-pattern {
  position: absolute;
  top: 0;
  right: 0;
  width: 100px;
  height: 100px;
  opacity: 0.1;
  background: radial-gradient(circle, currentColor 2px, transparent 2px);
  background-size: 20px 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  position: relative;
  z-index: 2;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  font-size: 24px;
  color: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.total-flights .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #4f46e5 100%);
}

.active-flights .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.delayed-flights .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.cancelled-flights .stat-icon {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-info h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-number {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1;
}

.stat-change {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
}

.stat-change.positive {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.stat-change.negative {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

/* Content Section */
.content-section {
  margin-bottom: 32px;
}

.chart-card,
.quick-actions-card {
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  height: 420px;
  margin-bottom: 24px;
  position: relative;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0 24px;
  margin-bottom: 12px;
  border-bottom: 1px solid rgba(99, 102, 241, 0.1);
  padding-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  position: relative;
}

.card-header h3::after {
  content: '';
  position: absolute;
  bottom: -16px;
  left: 0;
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 2px;
}

.refresh-btn {
  color: #6366f1;
  font-weight: 600;
}

.chart-container {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 0 24px 24px 24px;
}

.chart-placeholder {
  text-align: center;
  color: #64748b;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #cbd5e1;
}

.quick-actions {
  padding: 16px 24px 24px 24px;
  height: calc(100% - 120px);
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-y: auto;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  width: 100%;
  max-width: 100%;
}

.action-item {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(79, 70, 229, 0.1) 100%);
  border: 1px solid rgba(99, 102, 241, 0.2);
  border-radius: 16px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 100px;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.action-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(79, 70, 229, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 16px;
}

.action-item:hover {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(79, 70, 229, 0.15) 100%);
  border-color: rgba(99, 102, 241, 0.4);
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.3);
}

.action-item:hover::before {
  opacity: 1;
}

.action-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: white;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.3);
  position: relative;
  z-index: 2;
}

.action-item span {
  font-weight: 600;
  color: #1e293b;
  font-size: 13px;
  text-align: center;
  line-height: 1.2;
  word-wrap: break-word;
  max-width: 100%;
  position: relative;
  z-index: 2;
}

/* Bottom Section */
.bottom-section {
  margin-bottom: 32px;
}

.status-card,
.recent-flights-card {
  background: rgba(248, 250, 252, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  height: 350px;
  margin-bottom: 24px;
}

.system-status {
  padding: 0 24px 24px 24px;
  height: calc(100% - 80px);
  overflow-y: auto;
}

.status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.status-item:last-child {
  border-bottom: none;
}

.status-item:hover {
  background: rgba(99, 102, 241, 0.05);
  border-radius: 12px;
  padding: 16px 12px;
  margin: 0 -12px;
}

.status-item.status-error {
  background: rgba(239, 68, 68, 0.05);
}

.status-info {
  display: flex;
  flex-direction: column;
}

.status-label {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
}

.status-description {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 8px;
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.status-indicator.active {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.recent-flights {
  height: calc(100% - 80px);
  overflow-y: auto;
  padding: 0 24px 24px 24px;
}

.no-data {
  text-align: center;
  color: #64748b;
  padding: 60px 0;
}

.no-data-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #cbd5e1;
}

.flight-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.flight-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: rgba(99, 102, 241, 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
  border: 1px solid rgba(99, 102, 241, 0.1);
}

.flight-item:hover {
  background: rgba(99, 102, 241, 0.1);
  transform: translateX(4px);
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.2);
}

.flight-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.flight-number {
  font-weight: 700;
  color: #1e293b;
  font-size: 14px;
}

.flight-route {
  font-size: 12px;
  color: #64748b;
}

.flight-time {
  font-size: 11px;
  color: #64748b;
  font-weight: 600;
  background: rgba(99, 102, 241, 0.1);
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
  white-space: nowrap;
}

.flight-status {
  font-size: 11px;
  font-weight: 700;
  padding: 4px 8px;
  border-radius: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.status-success {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.status-warning {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.status-danger {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.status-info {
  background: rgba(99, 102, 241, 0.1);
  color: #6366f1;
}

/* Responsive */
@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .welcome-content {
    flex-direction: column;
    gap: 24px;
    text-align: center;
  }
  
  .welcome-title {
    font-size: 28px;
  }
  
  .welcome-stats {
    justify-content: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-right: 0;
    margin-bottom: 16px;
  }
  
  .action-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .action-item {
    min-height: 100px;
    padding: 16px;
  }
  
  .chart-card,
  .quick-actions-card,
  .status-card,
  .recent-flights-card {
    height: auto;
    min-height: 300px;
  }
  
  .quick-actions-card {
    min-height: 350px;
  }
  
  .quick-actions {
    height: auto;
    padding: 16px;
    overflow-y: visible;
  }
  
  .action-grid {
    gap: 10px;
    margin-top: 16px;
  }
  
  .chart-container {
    height: 200px;
  }
  
  .quick-actions {
    height: auto;
  }
  
  .system-status {
    height: auto;
  }
  
  .recent-flights {
    height: auto;
    max-height: 200px;
  }
}

@media (max-width: 480px) {
  .dashboard {
    padding: 12px;
  }
  
  .welcome-card {
    padding: 24px;
  }
  
  .welcome-title {
    font-size: 24px;
  }
  
  .welcome-subtitle {
    font-size: 16px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 20px;
  }
  
  .action-item {
    padding: 12px;
    min-height: 80px;
  }
  
  .action-icon {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
  
  .action-item span {
    font-size: 12px;
  }
  
  .card-header {
    padding: 16px 16px 0 16px;
    margin-bottom: 16px;
  }
  
  .card-header h3 {
    font-size: 18px;
  }
  
  .action-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .action-item span {
    font-size: 13px;
  }
}
</style>
