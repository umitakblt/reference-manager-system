<template>
  <div class="flight-list-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">🛫 Uçuş Listesi</h1>
        <p class="page-subtitle">Tüm uçuşlarınızı görüntüleyin ve yönetin</p>
      </div>
      <div class="header-controls">
        <el-button @click="refreshFlights" :loading="loading" type="primary" class="control-btn">
          <el-icon><Refresh /></el-icon>
          Yenile
        </el-button>
        <el-button 
          v-if="canPerformAction('create-flight')"
          @click="goToCreateFlight" 
          type="success" 
          class="control-btn"
        >
          <el-icon><Plus /></el-icon>
          Yeni Uçuş
        </el-button>
      </div>
    </div>

    <div class="stats-section">
      <div class="stats-grid">
        <div class="stat-item glass-card">
          <div class="stat-icon-container total">
            <el-icon><Location /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ flights.length }}</h3>
            <p>Toplam Uçuş</p>
          </div>
        </div>
        <div class="stat-item glass-card">
          <div class="stat-icon-container active">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ activeFlights }}</h3>
            <p>Aktif Uçuş</p>
          </div>
        </div>
        <div class="stat-item glass-card">
          <div class="stat-icon-container completed">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ completedFlights }}</h3>
            <p>Tamamlanan</p>
          </div>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-content glass-card">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <p>Uçuşlar yükleniyor...</p>
      </div>
    </div>

    <div v-if="error" class="error-container">
      <div class="error-content glass-card">
        <el-icon class="error-icon"><Warning /></el-icon>
        <p>{{ error }}</p>
      </div>
    </div>

    <div v-if="flights.length && !loading" class="flights-section">
      <div class="section-header">
        <div class="header-left">
          <h2>Uçuş Detayları</h2>
          <div class="table-info">
            <span class="info-text">{{ filteredFlights.length }} uçuş listeleniyor</span>
          </div>
        </div>
        
        <div class="header-controls">
          <div class="controls-row">
            <el-input
              v-model="searchQuery"
              placeholder="Uçuş ara..."
              class="search-input"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            
            <el-select 
              v-model="sortBy" 
              placeholder="Sıralama kriteri"
              class="sort-select"
              @change="handleSortChange"
            >
              <el-option label="Uçuş No" value="flightNumber" />
              <el-option label="Kalkış Zamanı" value="scheduledDeparture" />
              <el-option label="Varış Zamanı" value="scheduledArrival" />
              <el-option label="Durum" value="status" />
              <el-option label="Havayolu" value="airlineId" />
              <el-option label="Kalkış İstasyonu" value="originStationId" />
              <el-option label="Varış İstasyonu" value="destinationStationId" />
            </el-select>
            
            <el-button 
              @click="toggleSortOrder" 
              :type="sortOrder === 'asc' ? 'primary' : 'default'"
              class="sort-btn"
              size="small"
            >
              <el-icon>
                <ArrowUp v-if="sortOrder === 'asc'" />
                <ArrowDown v-else />
              </el-icon>
              {{ sortOrder === 'asc' ? 'Artan' : 'Azalan' }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="table-container glass-card" :style="{ 
        width: tableWidth, 
        margin: '0 auto', 
        padding: '0',
        position: 'relative'
      }">
        <el-table 
          :data="filteredFlights" 
          style="width: 100%; min-width: 100%;" 
          v-loading="loading"
          class="flight-table"
          :row-class-name="getRowClassName"
          @row-click="viewFlightDetails"
          @sort-change="handleTableSort"
        >
          <el-table-column prop="flightNumber" label="Uçuş No" width="280" fixed="left" sortable="custom">
            <template #default="scope">
              <div class="flight-number-cell">
                <span class="flight-code">{{ scope.row.flightNumber }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="originStationId" label="Kalkış İstasyonu" width="260" sortable="custom">
            <template #default="scope">
              <div class="station-cell">
                <div class="station-code">{{ getStationCode(scope.row.originStationId) }}</div>
                <div class="station-name">{{ getStationName(scope.row.originStationId, stations) }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="destinationStationId" label="Varış İstasyonu" width="260" sortable="custom">
            <template #default="scope">
              <div class="station-cell">
                <div class="station-code">{{ getStationCode(scope.row.destinationStationId) }}</div>
                <div class="station-name">{{ getStationName(scope.row.destinationStationId, stations) }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="airlineId" label="Havayolu" width="260" sortable="custom">
            <template #default="scope">
              <div class="airline-cell">
                <el-icon><Location /></el-icon>
                <span>{{ getAirlineName(scope.row.airlineId, airlines) }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="aircraftId" label="Uçak" width="260" sortable="custom">
            <template #default="scope">
              <div class="aircraft-cell">
                <el-icon><Document /></el-icon>
                <div class="aircraft-info">
                  <div class="aircraft-name">{{ getAircraftName(scope.row.aircraftId, aircrafts) }}</div>
                  <div class="aircraft-details">{{ getAircraftDetails(scope.row.aircraftId, aircrafts) }}</div>
                  <div class="aircraft-debug" style="font-size: 10px; color: #999;">ID: {{ scope.row.aircraftId }}</div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="scheduledDeparture" label="Kalkış" width="260" sortable="custom">
            <template #default="scope">
              <div class="time-cell">
                <div class="time">{{ formatTime(scope.row.scheduledDeparture) }}</div>
                <div class="date">{{ formatDate(scope.row.scheduledDeparture) }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="scheduledArrival" label="Varış" width="260" sortable="custom">
            <template #default="scope">
              <div class="time-cell">
                <div class="time">{{ formatTime(scope.row.scheduledArrival) }}</div>
                <div class="date">{{ formatDate(scope.row.scheduledArrival) }}</div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="Durum" width="200" sortable="custom">
            <template #default="scope">
              <div class="status-cell">
                <el-tag 
                  :type="getStatusType(scope.row.status)"
                  class="status-tag"
                  size="small"
                >
                  {{ getStatusText(scope.row.status) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="İşlemler" width="320" fixed="right" v-if="canPerformAction('edit-flight')">
            <template #default="scope">
              <div class="actions-cell">
                <el-button type="primary" size="small" @click.stop="editFlight(scope.row)">
                  <el-icon><Edit /></el-icon>
                  Düzenle
                </el-button>
                <el-button type="danger" size="small" @click.stop="deleteFlight(scope.row.id)">
                  <el-icon><Delete /></el-icon>
                  Sil
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <div v-if="!flights.length && !loading" class="empty-state">
      <div class="empty-content glass-card">
        <el-icon class="empty-icon"><Location /></el-icon>
        <h3>Henüz uçuş bulunmuyor</h3>
        <p>İlk uçuşunuzu ekleyerek başlayın</p>
        <el-button 
          v-if="canPerformAction('create-flight')"
          type="primary" 
          @click="goToCreateFlight"
        >
          <el-icon><Plus /></el-icon>
          Yeni Uçuş Ekle
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, inject } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../services/api'
import { getAirlineName, getAircraftName, getStationName } from '../utils/lookup'
import { canPerformAction } from '../services/authService'
import { 
  Refresh, 
  Plus, 
  Location, 
  Clock, 
  Check, 
  Warning, 
  Loading,
  Search,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  Document,
  Calendar,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import nativeWebSocketService from '../services/nativeWebSocketService'

const router = useRouter()
const flights = ref([])
const loading = ref(false)
const error = ref('')
const airlines = ref([])
const aircrafts = ref([])
const stations = ref([])
const searchQuery = ref('')
const sortBy = ref('scheduledDeparture')
const sortOrder = ref('asc')
const websocketSubscription = ref(null)

const fetchFlights = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await api.get('/v1/flights')
    flights.value = res.data
    
    flights.value.forEach(flight => {
      if (!flight.scheduledDeparture || !flight.scheduledArrival) {
        console.warn('Eksik tarih verisi:', flight)
      }
    })
  } catch (err) {
    error.value = 'Uçuşlar yüklenirken hata oluştu'
  } finally {
    loading.value = false
  }
}

const loadReferenceData = async () => {
  try {
    const [airlinesRes, aircraftsRes, stationsRes] = await Promise.all([
      api.get('/v1/airlines'),
      api.get('/v1/aircrafts'),
      api.get('/v1/stations')
    ])
    airlines.value = airlinesRes.data
    aircrafts.value = aircraftsRes.data
    stations.value = stationsRes.data
    
    // Debug log'ları
    console.log('📊 Airlines:', airlines.value)
    console.log('✈️ Aircrafts:', aircrafts.value)
    console.log('🏢 Stations:', stations.value)
  } catch (err) {
    console.error('Referans veriler yüklenirken hata:', err)
  }
}

const refreshFlights = () => {
  fetchFlights()
  ElMessage.success('Uçuş listesi yenilendi')
}

const goToCreateFlight = () => {
  router.push('/create-flight')
}

const viewFlightDetails = (flight) => {
  router.push(`/edit-flight/${flight.id}`)
}

const editFlight = (flight) => {
  router.push(`/edit-flight/${flight.id}`)
}

const deleteFlight = async (flightId) => {
  try {
    await ElMessageBox.confirm(
      'Bu uçuşu silmek istediğinizden emin misiniz?',
      'Uçuş Silme Onayı',
      {
        confirmButtonText: 'Evet, Sil',
        cancelButtonText: 'İptal',
        type: 'warning',
      }
    )
    
    const flightToDelete = flights.value.find(f => f.id === flightId)
    
            await api.delete(`/v1/flights/${flightId}`)
    await fetchFlights()
    ElMessage.success('Uçuş başarıyla silindi')
    
    if (flightToDelete) {
      try {
        console.log('📤 WebSocket ile uçuş silme güncellemesi gönderiliyor...')
        nativeWebSocketService.sendFlightUpdate('DELETE', flightToDelete)
        console.log('✅ WebSocket mesajı gönderildi')
      } catch (wsError) {
        console.error('❌ WebSocket mesajı gönderilemedi:', wsError)
      }
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('Uçuş silinirken hata oluştu')
    }
  }
}

const getStatusType = (status) => {
  const statusMap = {
    'ON_TIME': 'success',
    'DELAYED': 'warning',
    'CANCELLED': 'danger',
    'BOARDING': 'info',
    'PLANNED': 'info',
    'COMPLETED': 'success'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    'ON_TIME': 'Zamanında',
    'DELAYED': 'Gecikmeli',
    'CANCELLED': 'İptal',
    'BOARDING': 'Biniş',
    'PLANNED': 'Planlandı',
    'COMPLETED': 'Tamamlandı'
  }
  return statusMap[status] || status
}

const getStationCode = (stationId) => {
  const station = stations.value.find(s => s.id === stationId)
  return station ? station.code : stationId
}

const getAircraftDetails = (aircraftId, aircrafts) => {
  if (!Array.isArray(aircrafts)) return ''
  const aircraft = aircrafts.find(a => a.id === aircraftId || a.id === String(aircraftId) || a.id === Number(aircraftId))
  if (!aircraft) return ''
  
  const details = []
  if (aircraft.model) details.push(aircraft.model)
  if (aircraft.capacity) details.push(`${aircraft.capacity} koltuk`)
  
  return details.join(' • ')
}

const formatTime = (dateString) => {
  if (!dateString) return '--:--'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) {
      console.warn('Geçersiz tarih formatı:', dateString)
      return '--:--'
    }
    
    return date.toLocaleTimeString('tr-TR', { 
      hour: '2-digit', 
      minute: '2-digit' 
    })
  } catch (error) {
    console.error('Tarih formatlama hatası:', error, 'Tarih:', dateString)
    return '--:--'
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '--/--/----'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) {
      console.warn('Geçersiz tarih formatı:', dateString)
      return '--/--/----'
    }
    
    return date.toLocaleDateString('tr-TR', { 
      day: '2-digit', 
      month: '2-digit',
      year: 'numeric'
    })
  } catch (error) {
    console.error('Tarih formatlama hatası:', error, 'Tarih:', dateString)
    return '--/--/----'
  }
}

const handleSortChange = () => {
  sortOrder.value = 'asc'
}

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

const handleTableSort = ({ column, prop, order }) => {
  if (prop) {
    sortBy.value = prop
    sortOrder.value = order === 'ascending' ? 'asc' : 'desc'
  }
}

const sortFlights = (flightsToSort) => {
  if (!sortBy.value) return flightsToSort
  
  return flightsToSort.sort((a, b) => {
    let aValue, bValue
    
    switch (sortBy.value) {
      case 'flightNumber':
        aValue = a.flightNumber || ''
        bValue = b.flightNumber || ''
        break
      case 'scheduledDeparture':
        aValue = new Date(a.scheduledDeparture || 0)
        bValue = new Date(b.scheduledDeparture || 0)
        break
      case 'scheduledArrival':
        aValue = new Date(a.scheduledArrival || 0)
        bValue = new Date(b.scheduledArrival || 0)
        break
      case 'status':
        aValue = getStatusText(a.status) || ''
        bValue = getStatusText(b.status) || ''
        break
      case 'airlineId':
        aValue = getAirlineName(a.airlineId, airlines) || ''
        bValue = getAirlineName(b.airlineId, airlines) || ''
        break
      case 'originStationId':
        aValue = getStationName(a.originStationId, stations) || ''
        bValue = getStationName(b.originStationId, stations) || ''
        break
      case 'destinationStationId':
        aValue = getStationName(a.destinationStationId, stations) || ''
        bValue = getStationName(b.destinationStationId, stations) || ''
        break
      default:
        aValue = a[sortBy.value] || ''
        bValue = b[sortBy.value] || ''
    }
    
    if (typeof aValue === 'string' && typeof bValue === 'string') {
      aValue = aValue.toLowerCase()
      bValue = bValue.toLowerCase()
    }
    
    if (aValue instanceof Date && bValue instanceof Date) {
      aValue = aValue.getTime()
      bValue = bValue.getTime()
    }
    
    if (sortOrder.value === 'asc') {
      return aValue > bValue ? 1 : aValue < bValue ? -1 : 0
    } else {
      return aValue < bValue ? 1 : aValue > bValue ? -1 : 0
    }
  })
}

const filteredFlights = computed(() => {
  let filtered = flights.value
  
  if (searchQuery.value) {
    filtered = filtered.filter(flight => 
      flight.flightNumber.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      getAirlineName(flight.airlineId, airlines).toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      getStationName(flight.originStationId, stations).toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      getStationName(flight.destinationStationId, stations).toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  return sortFlights(filtered)
})

const activeFlights = computed(() => {
  return flights.value.filter(f => f.status === 'ON_TIME' || f.status === 'BOARDING').length
})

const completedFlights = computed(() => {
  return flights.value.filter(f => f.status === 'COMPLETED').length
})

const sidebarCollapsed = inject('sidebarCollapsed', ref(false))

const tableWidth = computed(() => {
  return sidebarCollapsed.value ? '100vw' : 'calc(100vw - 280px)'
})

const getRowClassName = ({ row }) => {
  if (row.status === 'COMPLETED') {
    return 'completed-row'
  }
  return ''
}

let connectionCheckInterval = null

onMounted(async () => {
  await loadReferenceData()
  await fetchFlights()

  console.log('🔄 FlightList sayfasında WebSocket dinleyicileri ekleniyor...')
  
  nativeWebSocketService.onMessage('FLIGHT_UPDATE', (data) => {
    console.log('📨 FLIGHT_UPDATE mesajı alındı:', data)
    
    if (data && data.flight && data.flight.flightNumber === 'TK123') {
      console.log('🚫 Test mesajı filtrelendi:', data.flight.flightNumber)
      return
    }
    
    handleFlightUpdate(data)
  })
  
  nativeWebSocketService.onConnectionChange((connected) => {
    if (connected) {
      console.log('✅ FlightList sayfasında WebSocket bağlantısı aktif')
      ElMessage.success('🔄 Gerçek zamanlı güncellemeler aktif')
    } else {
      console.log('❌ FlightList sayfasında WebSocket bağlantısı kesildi')
      ElMessage.warning('⚠️ Gerçek zamanlı güncellemeler devre dışı')
    }
  })
  
  const stats = nativeWebSocketService.getStats()
  console.log('📊 WebSocket istatistikleri:', stats)
  
  connectionCheckInterval = setInterval(() => {
    if (!nativeWebSocketService.isConnected() && !nativeWebSocketService.isConnecting) {
      console.log('🔍 Periyodik kontrol - WebSocket yeniden bağlanıyor...')
      nativeWebSocketService.connect().catch(console.error)
    }
  }, 30000)
})

onUnmounted(() => {
  if (connectionCheckInterval) {
    clearInterval(connectionCheckInterval)
  }
  console.log('🚪 FlightList sayfası kapatılıyor - WebSocket handler\'ları temizleniyor')
  nativeWebSocketService.offMessage('FLIGHT_UPDATE')
  console.log('✅ FlightList sayfası WebSocket temizlendi')
})

const handleFlightUpdate = (message) => {
  console.log('🔍 Flight update işleniyor:', message)
  
  try {
    if (message && typeof message === 'object') {
      let action, flight
      
      if (message.data && message.data.action && message.data.flight) {
        action = message.data.action
        flight = message.data.flight
        console.log('📨 Backend mesaj formatı tespit edildi')
      } else if (message.action && message.flight) {
        action = message.action
        flight = message.flight
        console.log('📨 Test mesaj formatı tespit edildi')
      } else {
        console.log('⚠️ Tanınmayan mesaj formatı:', message)
        return
      }
      
      if (action === 'CREATE') {
        const existingFlight = flights.value.find(f => f.id === flight.id)
        if (existingFlight) {
          console.log('🚫 Duplicate CREATE mesajı filtrelendi - uçuş zaten mevcut:', flight.id)
          return
        }
      }
      
      console.log(`🎯 İşlenen action: ${action}`)
      console.log(`✈️ İşlenen flight:`, flight)
      
      switch (action) {
        case 'CREATE':
          console.log('➕ CREATE işlemi - Yeni uçuş ekleniyor')
          
          const insertIndex = flights.value.findIndex(f => 
            new Date(f.scheduledDeparture) > new Date(flight.scheduledDeparture)
          )
          
          if (insertIndex === -1) {
            flights.value.push(flight)
          } else {
            flights.value.splice(insertIndex, 0, flight)
          }
          
          ElMessage.success(`✅ Yeni uçuş eklendi: ${flight.flightNumber}`)
          break
          
        case 'UPDATE':
          console.log('🔄 UPDATE işlemi - Uçuş güncelleniyor')
          const updateIndex = flights.value.findIndex(f => f.id === flight.id)
          if (updateIndex !== -1) {
            flights.value[updateIndex] = flight
            ElMessage.info(`🔄 Uçuş güncellendi: ${flight.flightNumber}`)
          } else {
            console.log('⚠️ Güncellenecek uçuş bulunamadı:', flight.id)
          }
          break
          
        case 'DELETE':
          console.log('🗑️ DELETE işlemi - Uçuş siliniyor')
          const deleteIndex = flights.value.findIndex(f => f.id === flight.id)
          if (deleteIndex !== -1) {
            flights.value.splice(deleteIndex, 1)
            ElMessage.warning(`🗑️ Uçuş silindi: ${flight.flightNumber}`)
          } else {
            console.log('⚠️ Silinecek uçuş bulunamadı:', flight.id)
          }
          break
          
        default:
          console.log('❓ Bilinmeyen action:', action)
      }
    } else {
      console.log('⚠️ Geçersiz mesaj formatı:', message)
    }
  } catch (error) {
    console.error('❌ Flight update işleme hatası:', error)
  }
}


</script>

<style scoped>
.flight-list-container {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 32px;
  padding: 0;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

.header-controls {
  display: flex;
  gap: 12px;
}

.control-btn {
  border-radius: 12px;
  padding: 12px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.control-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.stats-section {
  margin-bottom: 32px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  padding: 20px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-icon-container {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.stat-icon-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 12px;
}

.stat-icon-container.total {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
}

.stat-icon-container.active {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-icon-container.completed {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon-container .el-icon {
  font-size: 20px;
  color: white;
  z-index: 1;
}

.stat-info h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.stat-info p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.loading-content,
.error-content {
  padding: 40px;
  text-align: center;
  border-radius: 16px;
}

.loading-icon,
.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.loading-icon {
  color: #6366f1;
  animation: spin 1s linear infinite;
}

.error-icon {
  color: #ef4444;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.flights-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  gap: 20px;
}

.header-left {
  flex: 1;
}

.section-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.table-info {
  margin-top: 4px;
}

.info-text {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.header-controls {
  display: flex;
  align-items: center;
}

.controls-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 250px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.sort-select {
  width: 180px;
}

.sort-select :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.sort-btn {
  border-radius: 12px;
  padding: 10px 16px;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.3s ease;
  height: 40px;
}

.sort-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.table-container {
  padding: 0;
  border-radius: 0;
  border: none;
  transition: all 0.3s ease;
  overflow-x: auto;
  width: 100%;
  max-width: 100%;
  box-sizing: border-box;
  margin: 0 auto;
}

.table-container:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.flight-table :deep(.el-table__header-wrapper) {
  background-color: #f8f9fa;
}

.flight-table :deep(.el-table__header th) {
  background-color: #f8f9fa;
  font-weight: 700;
  color: #1e293b;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
  font-size: 14px;
}

.flight-table :deep(.el-table__inner-wrapper) {
  border-radius: 12px;
  overflow: hidden;
}

.flight-table :deep(.el-table__cell) {
  padding: 28px 24px;
  text-align: center;
  font-size: 14px;
  color: #495057;
  border-bottom: 1px solid #ebeef5;
}

.flight-table :deep(.el-table__cell.is-leaf) {
  padding-left: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:first-child) {
  padding-left: 0;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child) {
  padding-right: 0;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .flight-number-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .station-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .airline-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .aircraft-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .time-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .actions-cell) {
  padding-right: 20px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .status-tag) {
  margin-left: 10px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .time) {
  margin-bottom: 4px;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .date) {
  font-size: 12px;
  color: #64748b;
}

.flight-table :deep(.el-table__cell.is-leaf:last-child .actions-cell .el-button) {
  margin-left: 8px;
}

.flight-number-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 240px;
  padding: 18px 0;
}

.flight-code {
  font-size: 18px;
  font-weight: 700;
  color: #6366f1;
}

.status-tag {
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 28px;
  white-space: nowrap;
  min-width: 140px;
  text-align: center;
  font-size: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: inline-block;
  line-height: 1.2;
}

.station-cell {
  text-align: center;
  padding: 4px 0;
}

.station-code {
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  display: block;
  margin-bottom: 4px;
}

.station-name {
  font-size: 12px;
  color: #64748b;
  display: block;
}

.airline-cell,
.aircraft-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  min-width: 220px;
  padding: 12px 0;
}

.airline-cell .el-icon,
.aircraft-cell .el-icon {
  font-size: 16px;
  color: #6366f1;
}

.time-cell {
  text-align: center;
  padding: 12px 0;
  min-height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 220px;
}

.time {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.date {
  font-size: 13px;
  color: #64748b;
  line-height: 1.2;
  font-weight: 500;
}

/* Placeholder styling for invalid dates */
.time:empty::before,
.date:empty::before {
  content: '--';
  color: #cbd5e1;
  font-weight: normal;
}

.actions-cell {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.actions-cell .el-button {
  border-radius: 8px;
  padding: 6px 12px;
  font-weight: 600;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.empty-content {
  padding: 60px 40px;
  text-align: center;
  border-radius: 16px;
}

.empty-icon {
  font-size: 64px;
  color: #6366f1;
  margin-bottom: 20px;
}

.empty-content h3 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.empty-content p {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 24px 0;
}

/* Responsive */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .header-controls {
    width: 100%;
    align-items: stretch;
  }
  
  .controls-row {
    flex-direction: column;
    gap: 12px;
    width: 100%;
  }
  
  .search-input {
    width: 100%;
  }
  
  .sort-select {
    width: 100%;
  }
  
  .table-container {
    padding: 16px;
    overflow-x: auto;
  }
  
  .flight-table :deep(.el-table) {
    font-size: 12px;
  }
  
  .flight-table :deep(.el-table__cell) {
    padding: 8px 4px;
  }
  
  .flight-number-cell {
    min-width: 120px;
    gap: 8px;
  }
  
  .flight-code {
    font-size: 14px;
  }
  
  .status-tag {
    padding: 2px 6px;
    font-size: 10px;
  }
  
  .station-code {
    font-size: 14px;
  }
  
  .aircraft-name {
    font-size: 12px;
  }
  
  .aircraft-details {
    font-size: 10px;
  }
  
  .time {
    font-size: 14px;
  }
  
  .actions-cell {
    flex-direction: column;
    gap: 4px;
  }
  
  .actions-cell .el-button {
    padding: 4px 8px;
    font-size: 12px;
  }
}

/* Table specific styles */
.flight-table :deep(.el-table__row) {
  cursor: pointer;
  transition: all 0.3s ease;
}

.flight-table :deep(.el-table__row:hover) {
  background-color: rgba(99, 102, 241, 0.05) !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.flight-table :deep(.completed-row) {
  background-color: rgba(16, 185, 129, 0.05) !important;
}

.flight-table :deep(.completed-row:hover) {
  background-color: rgba(16, 185, 129, 0.1) !important;
}

/* Fixed columns styling */
.flight-table :deep(.el-table__fixed) {
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.flight-table :deep(.el-table__fixed-right) {
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}

/* Status tag styling */
.status-tag {
  border-radius: 6px;
  font-weight: 600;
  padding: 4px 8px;
  font-size: 11px;
  white-space: nowrap;
  flex-shrink: 0;
}

/* Cell content styling */
.flight-number-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 140px;
  padding: 8px 0;
}

.flight-code {
  font-size: 18px;
  font-weight: 700;
  color: #6366f1;
  white-space: nowrap;
}

.status-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px 0;
}

.status-tag {
  border-radius: 6px;
  font-weight: 600;
  padding: 4px 8px;
  font-size: 11px;
  white-space: nowrap;
  flex-shrink: 0;
}

.station-cell {
  text-align: center;
  padding: 12px 0;
  min-width: 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.station-code {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  display: block;
  line-height: 1.2;
}

.station-name {
  font-size: 13px;
  color: #64748b;
  display: block;
  line-height: 1.2;
  font-weight: 500;
}

.airline-cell,
.aircraft-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: #64748b;
  padding: 12px 0;
  min-width: 220px;
}

.aircraft-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.aircraft-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  line-height: 1.2;
}

.aircraft-details {
  font-size: 11px;
  color: #64748b;
  line-height: 1.2;
  text-align: center;
}

.time-cell {
  text-align: center;
  padding: 12px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  min-width: 220px;
}

.actions-cell {
  display: flex;
  gap: 8px;
  justify-content: center;
  padding: 4px 0;
}
</style> 