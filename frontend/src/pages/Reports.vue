<template>
  <div class="reports-container">
    <div class="page-header">
      <h1>Raporlar</h1>
      <p>Uçuş performansı ve istatistiklerini görüntüleyin</p>
    </div>

    <el-row :gutter="24">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Havayolu Performansı</span>
            </div>
          </template>
          <AirlinePerformanceChart />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Gecikme Oranları</span>
            </div>
          </template>
          <DelayRateChart />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" style="margin-top: 24px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Aylık Uçuş Sayıları</span>
            </div>
          </template>
          <TotalFlightsBarChart />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>En Yoğun Rotalar</span>
            </div>
          </template>
          <el-table :data="topRoutes" style="width: 100%">
            <el-table-column prop="route" label="Rota" />
            <el-table-column prop="flightCount" label="Uçuş Sayısı" width="120" />
            <el-table-column prop="avgDelay" label="Ort. Gecikme (dk)" width="150" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 24px;">
      <template #header>
        <div class="card-header">
          <span>Detaylı Raporlar</span>
          <div class="report-actions">
            <el-button type="primary" @click="exportReport">
              <el-icon><Download /></el-icon>
              Raporu İndir
            </el-button>
          </div>
        </div>
      </template>

      <el-form :model="reportFilters" inline>
        <el-form-item label="Başlangıç Tarihi">
          <el-date-picker
            v-model="reportFilters.startDate"
            type="date"
            placeholder="Başlangıç tarihi"
          />
        </el-form-item>
        <el-form-item label="Bitiş Tarihi">
          <el-date-picker
            v-model="reportFilters.endDate"
            type="date"
            placeholder="Bitiş tarihi"
          />
        </el-form-item>
        <el-form-item label="Havayolu">
          <el-select v-model="reportFilters.airline" placeholder="Tümü" clearable>
            <el-option
              v-for="airline in airlines"
              :key="airline.id"
              :label="airline.name"
              :value="airline.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="generateReport">Rapor Oluştur</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="reportData" style="width: 100%" v-loading="reportLoading">
        <el-table-column prop="date" label="Tarih" width="120" />
        <el-table-column prop="airline" label="Havayolu" width="150" />
        <el-table-column prop="totalFlights" label="Toplam Uçuş" width="120" />
        <el-table-column prop="onTimeFlights" label="Zamanında" width="120" />
        <el-table-column prop="delayedFlights" label="Gecikmeli" width="120" />
        <el-table-column prop="cancelledFlights" label="İptal" width="100" />
        <el-table-column prop="avgDelay" label="Ort. Gecikme (dk)" width="150" />
        <el-table-column prop="onTimeRate" label="Zamanında Oranı (%)" width="150" />
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import AirlinePerformanceChart from '../components/charts/AirlinePerformanceChart.vue'
import DelayRateChart from '../components/charts/DelayRateChart.vue'
import TotalFlightsBarChart from '../components/charts/TotalFlightsBarChart.vue'
import api from '../services/api'

const reportLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const reportData = ref([])
const topRoutes = ref([])
const airlines = ref([])

const reportFilters = ref({
  startDate: '',
  endDate: '',
  airline: ''
})

const loadAirlines = async () => {
  try {
    const response = await api.get('/v1/airlines')
    airlines.value = response.data
  } catch (error) {
    console.error('Havayolları yüklenirken hata:', error)
  }
}

const generateReport = async () => {
  reportLoading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...reportFilters.value
    }
    
            const response = await api.get('/v1/reports/flights', { params })
    reportData.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    console.error('Rapor oluşturulurken hata:', error)
    ElMessage.error('Rapor oluşturulamadı')
  } finally {
    reportLoading.value = false
  }
}

const loadTopRoutes = async () => {
  try {
            const response = await api.get('/v1/reports/top-routes')
    topRoutes.value = response.data
  } catch (error) {
    console.error('En yoğun rotalar yüklenirken hata:', error)
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  generateReport()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  generateReport()
}

const exportReport = async () => {
  try {
            const response = await api.get('/v1/reports/export', {
      params: reportFilters.value,
      responseType: 'blob'
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', 'flight-report.xlsx')
    document.body.appendChild(link)
    link.click()
    link.remove()
    
    ElMessage.success('Rapor başarıyla indirildi')
  } catch (error) {
    console.error('Rapor indirme hatası:', error)
    ElMessage.error('Rapor indirilemedi')
  }
}

onMounted(() => {
  loadAirlines()
  loadTopRoutes()
  generateReport()
})
</script>

<style scoped>
.reports-container {
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-actions {
  display: flex;
  gap: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>