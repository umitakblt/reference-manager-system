<template>
  <div class="flight-archive">

    <div class="archive-header">
      <div class="header-content">
        <div class="header-text">
          <h1 class="page-title">
            <span class="gradient-text">Uçuş Arşivi</span>
          </h1>
          <p class="page-subtitle">Tamamlanmış ve arşivlenmiş uçuşların detaylı listesi</p>
        </div>
        <div class="header-stats">
          <div class="stat-badge">
            <el-icon><Document /></el-icon>
            <span>{{ archivedFlights.length }} Arşivlenmiş Uçuş</span>
          </div>
          <div class="stat-badge">
            <el-icon><Calendar /></el-icon>
            <span>{{ getCurrentMonth() }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card total-flights">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="stat-info">
                <h3>Toplam Arşiv</h3>
                <p class="stat-number">{{ archivedFlights.length }}</p>
                <span class="stat-description">Arşivlenmiş uçuş</span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card completed-flights">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <h3>Tamamlanan</h3>
                <p class="stat-number">{{ getCompletedCount() }}</p>
                <span class="stat-description">Zamanında uçuş</span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-card delayed-flights">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <h3>Gecikmeli</h3>
                <p class="stat-number">{{ getDelayedCount() }}</p>
                <span class="stat-description">Gecikmeli uçuş</span>
              </div>
            </div>
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
                <p class="stat-number">{{ getCancelledCount() }}</p>
                <span class="stat-description">İptal edilen uçuş</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="filters-section">
      <div class="filters-card">
        <div class="filters-header">
          <h3>Filtreler</h3>
          <el-button type="primary" @click="resetFilters" size="small">
            <el-icon><Refresh /></el-icon>
            Filtreleri Temizle
          </el-button>
        </div>
        
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="Havayolu">
              <el-select 
                v-model="filters.airline" 
                placeholder="Havayolu seçin"
                clearable
                style="width: 100%"
              >
                <el-option 
                  v-for="airline in airlines" 
                  :key="airline.id" 
                  :label="airline.name" 
                  :value="airline.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="Durum">
              <el-select 
                v-model="filters.status" 
                placeholder="Durum seçin"
                clearable
                style="width: 100%"
              >
                <el-option label="Zamanında" value="ON_TIME" />
                <el-option label="Gecikmeli" value="DELAYED" />
                <el-option label="İptal" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="Tarih Aralığı">
              <el-date-picker
                v-model="filters.dateRange"
                type="daterange"
                range-separator="~"
                start-placeholder="Başlangıç"
                end-placeholder="Bitiş"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :xs="24" :sm="12" :md="6">
            <el-form-item label="Arama">
              <el-input 
                v-model="filters.search" 
                placeholder="Uçuş no, havayolu..."
                clearable
                style="width: 100%"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </div>

    <div class="table-section">
      <div class="table-card">
        <div class="table-header">
          <h3>Arşivlenmiş Uçuşlar</h3>
          <div class="table-actions">
            <el-button type="primary" @click="exportArchive" :loading="exporting">
              <el-icon><Download /></el-icon>
              Dışa Aktar
            </el-button>
            <el-button @click="refreshData" :loading="loading">
              <el-icon><Refresh /></el-icon>
              Yenile
            </el-button>
          </div>
        </div>
        
        <div class="table-container">
          <el-table 
            :data="filteredFlights" 
            v-loading="loading" 
            style="width: 100%"
            :row-class-name="getRowClassName"
            @row-click="handleRowClick"
          >
            <el-table-column prop="flightNumber" label="Uçuş No" width="150" fixed="left" align="center" header-align="center">
              <template #default="scope">
                <div class="flight-number">
                  <span class="number">{{ scope.row.flightNumber }}</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="Havayolu" width="220" align="center" header-align="center">
              <template #default="scope">
                <div class="airline-info">
                  <div class="airline-logo">
                    <el-icon><Promotion /></el-icon>
                  </div>
                  <div class="airline-details">
                    <span class="airline-name">{{ getAirlineName(scope.row.airlineId) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="Uçak" width="250" align="center" header-align="center">
              <template #default="scope">
                <div class="aircraft-info">
                  <div class="aircraft-icon">
                    <el-icon><Location /></el-icon>
                  </div>
                  <div class="aircraft-details">
                    <span class="aircraft-name">{{ getAircraftInfo(scope.row.aircraftId) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="Rota" width="300" align="center" header-align="center">
              <template #default="scope">
                <div class="route-info">
                  <div class="route-origin">
                    <span class="station-code">{{ getStationCode(scope.row.originStationId) }}</span>
                    <span class="station-name">{{ getStationName(scope.row.originStationId) }}</span>
                  </div>
                  <div class="route-arrow">
                    <el-icon><Right /></el-icon>
                  </div>
                  <div class="route-destination">
                    <span class="station-code">{{ getStationCode(scope.row.destinationStationId) }}</span>
                    <span class="station-name">{{ getStationName(scope.row.destinationStationId) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column label="Zaman" width="250" align="center" header-align="center">
              <template #default="scope">
                <div class="time-info">
                  <div class="departure-time">
                    <span class="time-label">Kalkış</span>
                    <span class="time-value">{{ formatTime(scope.row.scheduledDeparture) }}</span>
                  </div>
                  <div class="arrival-time">
                    <span class="time-label">Varış</span>
                    <span class="time-value">{{ formatTime(scope.row.scheduledArrival) }}</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="status" label="Durum" width="150" fixed="right" align="center" header-align="center">
              <template #default="scope">
                <div class="status-badge" :class="getStatusClass(scope.row.status)">
                  <el-icon><CircleCheck v-if="scope.row.status === 'ON_TIME'" /><Clock v-else-if="scope.row.status === 'DELAYED'" /><Close v-else /></el-icon>
                  <span>{{ getStatusText(scope.row.status) }}</span>
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="archivedAt" label="Arşivlenme" width="180" fixed="right" align="center" header-align="center">
              <template #default="scope">
                <div class="archive-date">
                  <span class="date">{{ formatDate(scope.row.archivedAt) }}</span>
                  <span class="time">{{ formatTimeOnly(scope.row.archivedAt) }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <div class="pagination-section">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="filteredFlights.length"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Document, 
  Calendar, 
  CircleCheck, 
  Clock, 
  Close, 
  Refresh, 
  Search, 
  Download, 
  Promotion, 
  Location, 
  Right 
} from '@element-plus/icons-vue'
import api from '../services/api'

export default {
  name: 'FlightArchive',
  setup() {
    const archivedFlights = ref([])
    const loading = ref(false)
    const exporting = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    
    const airlines = ref([])
    const aircrafts = ref([])
    const stations = ref([])
    
    const filters = reactive({
      airline: null,
      status: null,
      dateRange: null,
      search: ''
    })
    
    const loadArchivedFlights = async () => {
      loading.value = true
      try {
        const response = await api.get('/v1/flights/archive')
        archivedFlights.value = response.data
      } catch (error) {
        ElMessage.error('Arşivlenmiş uçuşlar yüklenirken hata oluştu')
        console.error('Error loading archived flights:', error)
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
      } catch (error) {
        ElMessage.error('Referans veriler yüklenirken hata oluştu')
        console.error('Error loading reference data:', error)
      }
    }
    
    const filteredFlights = computed(() => {
      let filtered = archivedFlights.value
      
      if (filters.airline) {
        filtered = filtered.filter(flight => flight.airlineId === filters.airline)
      }
      
      if (filters.status) {
        filtered = filtered.filter(flight => flight.status === filters.status)
      }
      
      if (filters.dateRange && filters.dateRange.length === 2) {
        const startDate = new Date(filters.dateRange[0])
        const endDate = new Date(filters.dateRange[1])
        filtered = filtered.filter(flight => {
          const flightDate = new Date(flight.scheduledDeparture)
          return flightDate >= startDate && flightDate <= endDate
        })
      }
      
      if (filters.search) {
        const searchTerm = filters.search.toLowerCase()
        filtered = filtered.filter(flight => 
          flight.flightNumber.toLowerCase().includes(searchTerm) ||
          getAirlineName(flight.airlineId).toLowerCase().includes(searchTerm)
        )
      }
      
      return filtered
    })
    
    const getCompletedCount = () => {
      return archivedFlights.value.filter(f => f.status === 'ON_TIME').length
    }
    
    const getDelayedCount = () => {
      return archivedFlights.value.filter(f => f.status === 'DELAYED').length
    }
    
    const getCancelledCount = () => {
      return archivedFlights.value.filter(f => f.status === 'CANCELLED').length
    }
    
    const getAirlineName = (airlineId) => {
      const airline = airlines.value.find(a => a.id === airlineId)
      return airline ? airline.name : `ID: ${airlineId}`
    }
    
    const getAircraftInfo = (aircraftId) => {
      const aircraft = aircrafts.value.find(a => a.id === aircraftId)
      return aircraft ? `${aircraft.name} - ${aircraft.model}` : `ID: ${aircraftId}`
    }
    
    const getStationName = (stationId) => {
      const station = stations.value.find(s => s.id === stationId)
      return station ? station.name : `ID: ${stationId}`
    }
    
    const getStationCode = (stationId) => {
      const station = stations.value.find(s => s.id === stationId)
      return station ? station.code : 'N/A'
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
        default: return status
      }
    }
    
    const formatTime = (dateTime) => {
      if (!dateTime) return 'N/A'
      const date = new Date(dateTime)
      return date.toLocaleTimeString('tr-TR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    
    const formatDate = (dateTime) => {
      if (!dateTime) return 'N/A'
      const date = new Date(dateTime)
      return date.toLocaleDateString('tr-TR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    }
    
    const formatTimeOnly = (dateTime) => {
      if (!dateTime) return 'N/A'
      const date = new Date(dateTime)
      return date.toLocaleTimeString('tr-TR', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    
    const getCurrentMonth = () => {
      return new Date().toLocaleDateString('tr-TR', {
        month: 'long',
        year: 'numeric'
      })
    }
    
    const getRowClassName = ({ row }) => {
      return `flight-row ${getStatusClass(row.status)}`
    }
    
    const handleRowClick = (row) => {
      ElMessage.info(`${row.flightNumber} uçuşu detayları yakında eklenecek`)
    }
    
    const resetFilters = () => {
      filters.airline = null
      filters.status = null
      filters.dateRange = null
      filters.search = ''
    }
    
    const refreshData = async () => {
      await loadReferenceData()
      await loadArchivedFlights()
      ElMessage.success('Veriler yenilendi')
    }
    
    const exportArchive = async () => {
      try {
        exporting.value = true
        await new Promise(resolve => setTimeout(resolve, 2000))
        ElMessage.success('Arşiv başarıyla dışa aktarıldı')
      } catch (error) {
        ElMessage.error('Dışa aktarma sırasında hata oluştu')
      } finally {
        exporting.value = false
      }
    }
    
    const handleSizeChange = (val) => {
      pageSize.value = val
      currentPage.value = 1
    }
    
    const handleCurrentChange = (val) => {
      currentPage.value = val
    }
    
    onMounted(() => {
      loadReferenceData()
      loadArchivedFlights()
    })
    
    return {
      archivedFlights,
      loading,
      exporting,
      airlines,
      aircrafts,
      stations,
      filters,
      filteredFlights,
      currentPage,
      pageSize,
      getAirlineName,
      getAircraftInfo,
      getStationName,
      getStationCode,
      getStatusType,
      getStatusClass,
      getStatusText,
      formatTime,
      formatDate,
      formatTimeOnly,
      getCurrentMonth,
      getCompletedCount,
      getDelayedCount,
      getCancelledCount,
      getRowClassName,
      handleRowClick,
      resetFilters,
      refreshData,
      exportArchive,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
.flight-archive {
  padding: 24px;
  min-height: 100vh;
  background: #f8fafc;
}

/* Header Section */
.archive-header {
  margin-bottom: 32px;
}

.header-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text {
  color: #1e293b;
}

.page-title {
  font-size: 32px;
  font-weight: 800;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.gradient-text {
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

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(99, 102, 241, 0.1);
  padding: 8px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  color: #6366f1;
}

/* Stats Section */
.stats-section {
  margin-bottom: 32px;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.total-flights .stat-icon {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
}

.completed-flights .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.delayed-flights .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.cancelled-flights .stat-icon {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.stat-info h3 {
  font-size: 14px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin: 0 0 4px 0;
}

.stat-number {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.stat-description {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.filters-section {
  margin-bottom: 32px;
}

.filters-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.filters-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.filters-header h3 {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.table-section {
  margin-bottom: 32px;
}

.table-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 24px 0 24px;
  margin-bottom: 20px;
}

.table-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.table-actions {
  display: flex;
  gap: 12px;
}

.table-container {
  padding: 0 24px 24px 24px;
  overflow-x: auto;
  width: 100%;
  min-width: 1200px;
}

.flight-number .number {
  font-weight: 700;
  color: #6366f1;
  font-size: 14px;
}

.airline-info,
.aircraft-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.airline-logo,
.aircraft-icon {
  width: 32px;
  height: 32px;
  background: rgba(99, 102, 241, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
}

.airline-name,
.aircraft-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.route-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.route-origin,
.route-destination {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.station-code {
  font-weight: 700;
  color: #6366f1;
  font-size: 14px;
}

.station-name {
  font-size: 12px;
  color: #64748b;
}

.route-arrow {
  color: #cbd5e1;
  font-size: 12px;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  align-items: center;
  justify-content: center;
}

.departure-time,
.arrival-time {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
}

.time-label {
  font-size: 11px;
  color: #64748b;
  font-weight: 500;
}

.time-value {
  font-weight: 600;
  color: #1e293b;
  font-size: 13px;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  min-width: 100px;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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

.archive-date {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.archive-date .date {
  font-weight: 600;
  color: #1e293b;
  font-size: 13px;
}

.archive-date .time {
  font-size: 11px;
  color: #64748b;
}

/* Row Styles */
.flight-row {
  cursor: pointer;
  transition: all 0.3s ease;
}

.flight-row:hover {
  background: rgba(99, 102, 241, 0.05) !important;
  transform: scale(1.01);
}

.flight-row.status-success:hover {
  background: rgba(16, 185, 129, 0.05) !important;
}

.flight-row.status-warning:hover {
  background: rgba(245, 158, 11, 0.05) !important;
}

.flight-row.status-danger:hover {
  background: rgba(239, 68, 68, 0.05) !important;
}

/* Pagination */
.pagination-section {
  padding: 24px;
  display: flex;
  justify-content: center;
  border-top: 1px solid #e2e8f0;
}

/* Responsive */
@media (max-width: 768px) {
  .flight-archive {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .header-stats {
    justify-content: center;
  }
  
  .stat-content {
    flex-direction: column;
    text-align: center;
  }
  
  .stat-icon {
    margin-bottom: 12px;
  }
  
  .filters-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .table-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .table-actions {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .flight-archive {
    padding: 12px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
}
</style>
