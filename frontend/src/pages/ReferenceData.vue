<template>
  <div class="reference-data">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="header-card">
          <template #header>
            <div class="header-content">
              <h2>Referans Veriler</h2>
              <p>Sistemdeki temel verileri yönetin</p>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- Havayolları -->
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <h3>Havayolları</h3>
              <el-button type="primary" size="small" @click="showAirlineDialog = true">
                <el-icon><Plus /></el-icon>
                Yeni Havayolu
              </el-button>
            </div>
          </template>
          
          <el-table :data="airlines" style="width: 100%" v-loading="loading.airlines">
            <el-table-column prop="name" label="Havayolu" />
            <el-table-column prop="code" label="Kod" width="100" />
            <el-table-column prop="country" label="Ülke" />
            <el-table-column label="İşlemler" width="120">
              <template #default="scope">
                <el-button link size="small" @click="editAirline(scope.row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" @click="deleteAirline(scope.row.id)" class="delete-btn">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- Uçaklar -->
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <h3>Uçaklar</h3>
              <el-button type="primary" size="small" @click="showAircraftDialog = true">
                <el-icon><Plus /></el-icon>
                Yeni Uçak
              </el-button>
            </div>
          </template>
          
          <el-table :data="aircrafts" style="width: 100%" v-loading="loading.aircrafts">
            <el-table-column prop="model" label="Model" />
            <el-table-column prop="manufacturer" label="Üretici" />
            <el-table-column prop="capacity" label="Kapasite" width="100" />
            <el-table-column label="İşlemler" width="120">
              <template #default="scope">
                <el-button link size="small" @click="editAircraft(scope.row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" @click="deleteAircraft(scope.row.id)" class="delete-btn">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- İstasyonlar -->
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <h3>İstasyonlar</h3>
              <el-button type="primary" size="small" @click="showStationDialog = true">
                <el-icon><Plus /></el-icon>
                Yeni İstasyon
              </el-button>
            </div>
          </template>
          
          <el-table :data="stations" style="width: 100%" v-loading="loading.stations">
            <el-table-column prop="name" label="İstasyon" />
            <el-table-column prop="code" label="Kod" width="100" />
            <el-table-column prop="city" label="Şehir" />
            <el-table-column label="İşlemler" width="120">
              <template #default="scope">
                <el-button link size="small" @click="editStation(scope.row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" @click="deleteStation(scope.row.id)" class="delete-btn">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- Uçuş Tipleri -->
      <el-col :span="12">
        <el-card class="data-card">
          <template #header>
            <div class="card-header">
              <h3>Uçuş Tipleri</h3>
              <el-button type="primary" size="small" @click="showFlightTypeDialog = true">
                <el-icon><Plus /></el-icon>
                Yeni Tip
              </el-button>
            </div>
          </template>
          
          <el-table :data="flightTypes" style="width: 100%" v-loading="loading.flightTypes">
            <el-table-column prop="name" label="Tip" />
            <el-table-column prop="description" label="Açıklama" />
            <el-table-column label="İşlemler" width="120">
              <template #default="scope">
                <el-button link size="small" @click="editFlightType(scope.row)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button link size="small" @click="deleteFlightType(scope.row.id)" class="delete-btn">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- Havayolu Dialog -->
    <el-dialog v-model="showAirlineDialog" title="Havayolu" width="500px">
      <el-form :model="airlineForm" :rules="airlineRules" ref="airlineFormRef" label-width="100px">
        <el-form-item label="Ad" prop="name">
          <el-input v-model="airlineForm.name" placeholder="Havayolu adı" />
        </el-form-item>
        <el-form-item label="Kod" prop="code">
          <el-input v-model="airlineForm.code" placeholder="IATA kodu" maxlength="3" />
        </el-form-item>
        <el-form-item label="Ülke" prop="country">
          <el-input v-model="airlineForm.country" placeholder="Ülke" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAirlineDialog = false">İptal</el-button>
        <el-button type="primary" @click="saveAirline" :loading="saving">Kaydet</el-button>
      </template>
    </el-dialog>

    <!-- Uçak Dialog -->
    <el-dialog v-model="showAircraftDialog" title="Uçak" width="500px">
      <el-form :model="aircraftForm" :rules="aircraftRules" ref="aircraftFormRef" label-width="100px">
        <el-form-item label="Ad" prop="name">
          <el-input v-model="aircraftForm.name" placeholder="Uçak adı" />
        </el-form-item>
        <el-form-item label="Model" prop="model">
          <el-input v-model="aircraftForm.model" placeholder="Uçak modeli" />
        </el-form-item>
        <el-form-item label="Üretici" prop="manufacturer">
          <el-input v-model="aircraftForm.manufacturer" placeholder="Üretici firma" />
        </el-form-item>
        <el-form-item label="Kapasite" prop="capacity">
          <el-input-number v-model="aircraftForm.capacity" :min="1" :max="1000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAircraftDialog = false">İptal</el-button>
        <el-button type="primary" @click="saveAircraft" :loading="saving">Kaydet</el-button>
      </template>
    </el-dialog>

    <!-- İstasyon Dialog -->
    <el-dialog v-model="showStationDialog" title="İstasyon" width="500px">
      <el-form :model="stationForm" :rules="stationRules" ref="stationFormRef" label-width="100px">
        <el-form-item label="Ad" prop="name">
          <el-input v-model="stationForm.name" placeholder="İstasyon adı" />
        </el-form-item>
        <el-form-item label="Kod" prop="code">
          <el-input v-model="stationForm.code" placeholder="IATA kodu" maxlength="3" />
        </el-form-item>
        <el-form-item label="Şehir" prop="city">
          <el-input v-model="stationForm.city" placeholder="Şehir" />
        </el-form-item>
        <el-form-item label="Ülke" prop="country">
          <el-input v-model="stationForm.country" placeholder="Ülke" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStationDialog = false">İptal</el-button>
        <el-button type="primary" @click="saveStation" :loading="saving">Kaydet</el-button>
      </template>
    </el-dialog>

    <!-- Uçuş Tipi Dialog -->
    <el-dialog v-model="showFlightTypeDialog" title="Uçuş Tipi" width="500px">
      <el-form :model="flightTypeForm" :rules="flightTypeRules" ref="flightTypeFormRef" label-width="100px">
        <el-form-item label="Tip" prop="name">
          <el-input v-model="flightTypeForm.name" placeholder="Uçuş tipi" />
        </el-form-item>
        <el-form-item label="Açıklama" prop="description">
          <el-input v-model="flightTypeForm.description" type="textarea" placeholder="Açıklama" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showFlightTypeDialog = false">İptal</el-button>
        <el-button type="primary" @click="saveFlightType" :loading="saving">Kaydet</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import api from '../services/api'

export default {
  name: 'ReferenceData',
  components: {
    Plus,
    Edit,
    Delete
  },
  setup() {
    const airlines = ref([])
    const aircrafts = ref([])
    const stations = ref([])
    const flightTypes = ref([])
    
    const loading = reactive({
      airlines: false,
      aircrafts: false,
      stations: false,
      flightTypes: false
    })
    
    const saving = ref(false)
    
    const showAirlineDialog = ref(false)
    const showAircraftDialog = ref(false)
    const showStationDialog = ref(false)
    const showFlightTypeDialog = ref(false)
    
    const airlineFormRef = ref()
    const aircraftFormRef = ref()
    const stationFormRef = ref()
    const flightTypeFormRef = ref()
    
    const airlineForm = reactive({
      id: null,
      name: '',
      code: '',
      country: ''
    })
    
    const aircraftForm = reactive({
      id: null,
      name: '',
      model: '',
      manufacturer: '',
      capacity: 100
    })
    
    const stationForm = reactive({
      id: null,
      name: '',
      code: '',
      city: '',
      country: ''
    })
    
    const flightTypeForm = reactive({
      id: null,
      name: '',
      description: ''
    })
    
    const airlineRules = {
      name: [{ required: true, message: 'Havayolu adı gerekli', trigger: 'blur' }],
      code: [{ required: true, message: 'Kod gerekli', trigger: 'blur' }],
      country: [{ required: true, message: 'Ülke gerekli', trigger: 'blur' }]
    }
    
    const aircraftRules = {
      name: [{ required: true, message: 'Uçak adı gerekli', trigger: 'blur' }],
      model: [{ required: true, message: 'Model gerekli', trigger: 'blur' }],
      manufacturer: [{ required: true, message: 'Üretici gerekli', trigger: 'blur' }],
      capacity: [{ required: true, message: 'Kapasite gerekli', trigger: 'blur' }]
    }
    
    const stationRules = {
      name: [{ required: true, message: 'İstasyon adı gerekli', trigger: 'blur' }],
      code: [{ required: true, message: 'Kod gerekli', trigger: 'blur' }],
      city: [{ required: true, message: 'Şehir gerekli', trigger: 'blur' }],
      country: [{ required: true, message: 'Ülke gerekli', trigger: 'blur' }]
    }
    
    const flightTypeRules = {
      name: [{ required: true, message: 'Tip adı gerekli', trigger: 'blur' }],
      description: [{ required: true, message: 'Açıklama gerekli', trigger: 'blur' }]
    }
    
    const loadAirlines = async () => {
      loading.airlines = true
      try {
        const response = await api.get('/v1/airlines')
        airlines.value = response.data
      } catch (error) {
        ElMessage.error('Havayolları yüklenirken hata oluştu')
      } finally {
        loading.airlines = false
      }
    }
    
    const loadAircrafts = async () => {
      loading.aircrafts = true
      try {
        const response = await api.get('/v1/aircrafts')
        aircrafts.value = response.data
      } catch (error) {
        ElMessage.error('Uçaklar yüklenirken hata oluştu')
      } finally {
        loading.aircrafts = false
      }
    }
    
    const loadStations = async () => {
      loading.stations = true
      try {
        const response = await api.get('/v1/stations')
        stations.value = response.data
      } catch (error) {
        ElMessage.error('İstasyonlar yüklenirken hata oluştu')
      } finally {
        loading.stations = false
      }
    }
    
    const loadFlightTypes = async () => {
      loading.flightTypes = true
      try {
        const response = await api.get('/v1/flight-types')
        flightTypes.value = response.data
      } catch (error) {
        ElMessage.error('Uçuş tipleri yüklenirken hata oluştu')
      } finally {
        loading.flightTypes = false
      }
    }
    
    const saveAirline = async () => {
      try {
        await airlineFormRef.value.validate()
        saving.value = true
        
        if (airlineForm.id) {
          console.log('🔄 Havayolu güncelleniyor:', airlineForm)
          const response = await api.put(`/v1/airlines/${airlineForm.id}`, airlineForm)
          console.log('✅ Havayolu güncelleme response:', response.data)
          
          // State'i hemen güncelle
          const airlineIndex = airlines.value.findIndex(a => a.id === airlineForm.id)
          if (airlineIndex !== -1) {
            airlines.value[airlineIndex] = { ...airlines.value[airlineIndex], ...response.data }
            console.log('✅ State güncellendi:', airlines.value[airlineIndex])
          }
          ElMessage.success('Havayolu güncellendi')
        } else {
          console.log('➕ Yeni havayolu ekleniyor:', JSON.stringify(airlineForm, null, 2))
          const response = await api.post('/v1/airlines', airlineForm)
          console.log('✅ Havayolu ekleme response:', response.data)
          
          // State'e hemen ekle
          airlines.value.push(response.data)
          console.log('✅ State\'e eklendi, toplam:', airlines.value.length)
          ElMessage.success('Havayolu eklendi')
        }
        
        showAirlineDialog.value = false
        resetAirlineForm()
      } catch (error) {
        ElMessage.error('Havayolu kaydedilirken hata oluştu')
      } finally {
        saving.value = false
      }
    }
    
    const saveAircraft = async () => {
      try {
        await aircraftFormRef.value.validate()
        saving.value = true
        
        if (aircraftForm.id) {
          console.log('🔄 Uçak güncelleniyor:', JSON.stringify(aircraftForm, null, 2))
          const response = await api.put(`/v1/aircrafts/${aircraftForm.id}`, aircraftForm)
          console.log('✅ Uçak güncelleme response:', response.data)
          
          // State'i hemen güncelle
          const aircraftIndex = aircrafts.value.findIndex(a => a.id === aircraftForm.id)
          if (aircraftIndex !== -1) {
            aircrafts.value[aircraftIndex] = { ...aircrafts.value[aircraftIndex], ...response.data }
            console.log('✅ State güncellendi:', aircrafts.value[aircraftIndex])
          }
          ElMessage.success('Uçak güncellendi')
        } else {
          console.log('➕ Yeni uçak ekleniyor:', JSON.stringify(aircraftForm, null, 2))
          const response = await api.post('/v1/aircrafts', aircraftForm)
          console.log('✅ Uçak ekleme response:', response.data)
          
          // State'e hemen ekle
          aircrafts.value.push(response.data)
          console.log('✅ State\'e eklendi, toplam:', aircrafts.value.length)
          ElMessage.success('Uçak eklendi')
        }
        
        // Modal'ı kapat ve formu sıfırla
        showAircraftDialog.value = false
        resetAircraftForm()
        
        // Form validasyonunu temizle
        if (aircraftFormRef.value) {
          aircraftFormRef.value.clearValidate()
        }
      } catch (error) {
        console.error('Uçak kaydetme hatası:', error)
        ElMessage.error('Uçak kaydedilirken hata oluştu')
      } finally {
        saving.value = false
      }
    }
    
    const saveStation = async () => {
      try {
        await stationFormRef.value.validate()
        saving.value = true
        
        if (stationForm.id) {
          console.log('🔄 İstasyon güncelleniyor:', JSON.stringify(stationForm, null, 2))
          const response = await api.put(`/v1/stations/${stationForm.id}`, stationForm)
          console.log('✅ İstasyon güncelleme response:', response.data)
          
          // State'i hemen güncelle
          const stationIndex = stations.value.findIndex(s => s.id === stationForm.id)
          if (stationIndex !== -1) {
            stations.value[stationIndex] = { ...stations.value[stationIndex], ...response.data }
            console.log('✅ State güncellendi:', stations.value[stationIndex])
          }
          ElMessage.success('İstasyon güncellendi')
        } else {
          console.log('➕ Yeni istasyon ekleniyor:', JSON.stringify(stationForm, null, 2))
          const response = await api.post('/v1/stations', stationForm)
          console.log('✅ İstasyon ekleme response:', response.data)
          
          // State'e hemen ekle
          stations.value.push(response.data)
          console.log('✅ State\'e eklendi, toplam:', stations.value.length)
          ElMessage.success('İstasyon eklendi')
        }
        
        showStationDialog.value = false
        resetStationForm()
      } catch (error) {
        ElMessage.error('İstasyon kaydedilirken hata oluştu')
      } finally {
        saving.value = false
      }
    }
    
    const saveFlightType = async () => {
      try {
        await flightTypeFormRef.value.validate()
        saving.value = true
        
        if (flightTypeForm.id) {
          console.log('🔄 Uçuş tipi güncelleniyor:', flightTypeForm)
          const response = await api.put(`/v1/flight-types/${flightTypeForm.id}`, flightTypeForm)
          console.log('✅ Uçuş tipi güncelleme response:', response.data)
          
          // State'i hemen güncelle
          const flightTypeIndex = flightTypes.value.findIndex(ft => ft.id === flightTypeForm.id)
          if (flightTypeIndex !== -1) {
            flightTypes.value[flightTypeIndex] = { ...flightTypes.value[flightTypeIndex], ...response.data }
            console.log('✅ State güncellendi:', flightTypes.value[flightTypeIndex])
          }
          ElMessage.success('Uçuş tipi güncellendi')
        } else {
          console.log('➕ Yeni uçuş tipi ekleniyor:', flightTypeForm)
          const response = await api.post('/v1/flight-types', flightTypeForm)
          console.log('✅ Uçuş tipi ekleme response:', response.data)
          
          // State'e hemen ekle
          flightTypes.value.push(response.data)
          console.log('✅ State\'e eklendi, toplam:', flightTypes.value.length)
          ElMessage.success('Uçuş tipi eklendi')
        }
        
        showFlightTypeDialog.value = false
        resetFlightTypeForm()
      } catch (error) {
        ElMessage.error('Uçuş tipi kaydedilirken hata oluştu')
      } finally {
        saving.value = false
      }
    }
    
    const editAirline = (airline) => {
      Object.assign(airlineForm, airline)
      showAirlineDialog.value = true
    }
    
    const editAircraft = (aircraft) => {
      Object.assign(aircraftForm, aircraft)
      showAircraftDialog.value = true
    }
    
    const editStation = (station) => {
      Object.assign(stationForm, station)
      showStationDialog.value = true
    }
    
    const editFlightType = (flightType) => {
      Object.assign(flightTypeForm, flightType)
      showFlightTypeDialog.value = true
    }
    
    const deleteAirline = async (id) => {
      try {
        await ElMessageBox.confirm('Bu havayolunu silmek istediğinizden emin misiniz?', 'Onay', {
          type: 'warning'
        })
        await api.delete(`/v1/airlines/${id}`)
        // State'den hemen sil
        const airlineIndex = airlines.value.findIndex(a => a.id === id)
        if (airlineIndex !== -1) {
          airlines.value.splice(airlineIndex, 1)
        }
        ElMessage.success('Havayolu silindi')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('Havayolu silinirken hata oluştu')
        }
      }
    }
    
    const deleteAircraft = async (id) => {
      try {
        await ElMessageBox.confirm('Bu uçağı silmek istediğinizden emin misiniz?', 'Onay', {
          type: 'warning'
        })
        await api.delete(`/v1/aircrafts/${id}`)
        // State'den hemen sil
        const aircraftIndex = aircrafts.value.findIndex(a => a.id === id)
        if (aircraftIndex !== -1) {
          aircrafts.value.splice(aircraftIndex, 1)
        }
        ElMessage.success('Uçak silindi')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('Uçak silinirken hata oluştu')
        }
      }
    }
    
    const deleteStation = async (id) => {
      try {
        await ElMessageBox.confirm('Bu istasyonu silmek istediğinizden emin misiniz?', 'Onay', {
          type: 'warning'
        })
        await api.delete(`/v1/stations/${id}`)
        // State'den hemen sil
        const stationIndex = stations.value.findIndex(s => s.id === id)
        if (stationIndex !== -1) {
          stations.value.splice(stationIndex, 1)
        }
        ElMessage.success('İstasyon silindi')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('İstasyon silinirken hata oluştu')
        }
      }
    }
    
    const deleteFlightType = async (id) => {
      try {
        await ElMessageBox.confirm('Bu uçuş tipini silmek istediğinizden emin misiniz?', 'Onay', {
          type: 'warning'
        })
        await api.delete(`/v1/flight-types/${id}`)
        // State'den hemen sil
        const flightTypeIndex = flightTypes.value.findIndex(ft => ft.id === id)
        if (flightTypeIndex !== -1) {
          flightTypes.value.splice(flightTypeIndex, 1)
        }
        ElMessage.success('Uçuş tipi silindi')
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('Uçuş tipi silinirken hata oluştu')
        }
      }
    }
    
    const resetAirlineForm = () => {
      airlineForm.id = null
      airlineForm.name = ''
      airlineForm.code = ''
      airlineForm.country = ''
    }
    
    const resetAircraftForm = () => {
      aircraftForm.id = null
      aircraftForm.name = ''
      aircraftForm.model = ''
      aircraftForm.manufacturer = ''
      aircraftForm.capacity = 100
    }
    
    const resetStationForm = () => {
      stationForm.id = null
      stationForm.name = ''
      stationForm.code = ''
      stationForm.city = ''
      stationForm.country = ''
    }
    
    const resetFlightTypeForm = () => {
      flightTypeForm.id = null
      flightTypeForm.name = ''
      flightTypeForm.description = ''
    }
    
    onMounted(() => {
      loadAirlines()
      loadAircrafts()
      loadStations()
      loadFlightTypes()
    })
    
    return {
      airlines,
      aircrafts,
      stations,
      flightTypes,
      loading,
      saving,
      showAirlineDialog,
      showAircraftDialog,
      showStationDialog,
      showFlightTypeDialog,
      airlineFormRef,
      aircraftFormRef,
      stationFormRef,
      flightTypeFormRef,
      airlineForm,
      aircraftForm,
      stationForm,
      flightTypeForm,
      airlineRules,
      aircraftRules,
      stationRules,
      flightTypeRules,
      saveAirline,
      saveAircraft,
      saveStation,
      saveFlightType,
      editAirline,
      editAircraft,
      editStation,
      editFlightType,
      deleteAirline,
      deleteAircraft,
      deleteStation,
      deleteFlightType
    }
  }
}
</script>

<style scoped>
.reference-data {
  padding: 20px;
}

.header-card {
  background: linear-gradient(135deg, #667eea 0%, #4f46e5 100%);
  color: white;
}

.header-content h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.header-content p {
  margin: 8px 0 0 0;
  opacity: 0.9;
  font-size: 14px;
}

.data-card {
  height: 100%;
  transition: all 0.3s ease;
}

.data-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.delete-btn {
  color: #f56c6c;
}

.delete-btn:hover {
  color: #e74c3c;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
}

.el-dialog {
  border-radius: 12px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  border-radius: 8px;
}

.el-input,
.el-input-number {
  border-radius: 8px;
}

/* Responsive */
@media (max-width: 768px) {
  .reference-data {
    padding: 10px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style> 