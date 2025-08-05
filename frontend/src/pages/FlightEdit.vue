<template>
  <div class="flight-edit">
    <el-card>
      <template #header>
        <h2>Uçuş Düzenle</h2>
      </template>
      
      <div v-if="loading" class="loading">
        <el-skeleton :rows="10" animated />
      </div>
      
      <el-form 
        v-else
        :model="flightForm" 
        :rules="rules" 
        ref="flightFormRef" 
        label-width="120px"
        @submit.prevent="handleSubmit"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Uçuş No" prop="flightNumber">
              <el-input v-model="flightForm.flightNumber" placeholder="Uçuş numarası" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="Havayolu" prop="airlineId">
              <el-select v-model="flightForm.airlineId" placeholder="Havayolu seçin" style="width: 100%">
                <el-option 
                  v-for="airline in airlines" 
                  :key="airline.id" 
                  :label="airline.name" 
                  :value="airline.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Uçak" prop="aircraftId">
              <el-select v-model="flightForm.aircraftId" placeholder="Uçak seçin" style="width: 100%">
                <el-option 
                  v-for="aircraft in aircrafts" 
                  :key="aircraft.id" 
                  :label="aircraft.registration" 
                  :value="aircraft.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="Uçuş Tipi" prop="flightTypeId">
              <el-select v-model="flightForm.flightTypeId" placeholder="Uçuş tipi seçin" style="width: 100%">
                <el-option 
                  v-for="type in flightTypes" 
                  :key="type.id" 
                  :label="type.name" 
                  :value="type.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Kalkış İstasyonu" prop="originStationId">
              <el-select v-model="flightForm.originStationId" placeholder="Kalkış istasyonu seçin" style="width: 100%">
                <el-option 
                  v-for="station in stations" 
                  :key="station.id" 
                  :label="station.name" 
                  :value="station.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="Varış İstasyonu" prop="destinationStationId">
              <el-select v-model="flightForm.destinationStationId" placeholder="Varış istasyonu seçin" style="width: 100%">
                <el-option 
                  v-for="station in stations" 
                  :key="station.id" 
                  :label="station.name" 
                  :value="station.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Kalkış Zamanı" prop="scheduledDeparture">
              <el-date-picker
                v-model="flightForm.scheduledDeparture"
                type="datetime"
                placeholder="Kalkış zamanı seçin"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="Varış Zamanı" prop="scheduledArrival">
              <el-date-picker
                v-model="flightForm.scheduledArrival"
                type="datetime"
                placeholder="Varış zamanı seçin"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Durum" prop="status">
              <el-select v-model="flightForm.status" placeholder="Durum seçin" style="width: 100%">
                <el-option label="Zamanında" value="ON_TIME" />
                <el-option label="Gecikmeli" value="DELAYED" />
                <el-option label="İptal" value="CANCELLED" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="Açıklama" prop="description">
              <el-input v-model="flightForm.description" placeholder="Açıklama" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            Güncelle
          </el-button>
          <el-button @click="$router.push('/flights')">
            İptal
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../services/api'
import nativeWebSocketService from '@/services/nativeWebSocketService'

export default {
  name: 'FlightEdit',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const flightFormRef = ref()
    const loading = ref(true)
    const submitting = ref(false)
    
    const flightForm = reactive({
      flightNumber: '',
      airlineId: null,
      aircraftId: null,
      originStationId: null,
      destinationStationId: null,
      scheduledDeparture: '',
      scheduledArrival: '',
      flightTypeId: null,
      status: 'ON_TIME',
      description: ''
    })
    
    const airlines = ref([])
    const aircrafts = ref([])
    const stations = ref([])
    const flightTypes = ref([])
    
    const rules = {
      flightNumber: [
        { required: true, message: 'Uçuş numarası gerekli', trigger: 'blur' }
      ],
      airlineId: [
        { required: true, message: 'Havayolu seçimi gerekli', trigger: 'change' }
      ],
      aircraftId: [
        { required: true, message: 'Uçak seçimi gerekli', trigger: 'change' }
      ],
      originStationId: [
        { required: true, message: 'Kalkış istasyonu gerekli', trigger: 'change' }
      ],
      destinationStationId: [
        { required: true, message: 'Varış istasyonu gerekli', trigger: 'change' }
      ],
      scheduledDeparture: [
        { required: true, message: 'Kalkış zamanı gerekli', trigger: 'change' }
      ],
      scheduledArrival: [
        { required: true, message: 'Varış zamanı gerekli', trigger: 'change' }
      ],
      flightTypeId: [
        { required: true, message: 'Uçuş tipi gerekli', trigger: 'change' }
      ]
    }
    
    const loadFlight = async () => {
      try {
        const flightId = route.params.id
        const response = await api.get(`/v1/flights/${flightId}`)
        const flight = response.data
        
        Object.assign(flightForm, {
          flightNumber: flight.flightNumber,
          airlineId: flight.airlineId,
          aircraftId: flight.aircraftId,
          originStationId: flight.originStationId,
          destinationStationId: flight.destinationStationId,
          scheduledDeparture: flight.scheduledDeparture,
          scheduledArrival: flight.scheduledArrival,
          flightTypeId: Array.isArray(flight.flightTypeId) ? flight.flightTypeId[0] : flight.flightTypeId,
          status: flight.status,
          description: flight.description
        })
      } catch (error) {
        ElMessage.error('Uçuş bilgileri yüklenirken hata oluştu')
        console.error('Error loading flight:', error)
      }
    }
    
    const loadReferenceData = async () => {
      try {
        const [airlinesRes, aircraftsRes, stationsRes, flightTypesRes] = await Promise.all([
          api.get('/v1/airlines'),
          api.get('/v1/aircrafts'),
          api.get('/v1/stations'),
          api.get('/v1/flight-types')
        ])
        
        airlines.value = airlinesRes.data
        aircrafts.value = aircraftsRes.data
        stations.value = stationsRes.data
        flightTypes.value = flightTypesRes.data
      } catch (error) {
        ElMessage.error('Referans veriler yüklenirken hata oluştu')
        console.error('Error loading reference data:', error)
      }
    }
    
    const handleSubmit = async () => {
      try {
        await flightFormRef.value.validate()
        submitting.value = true
        
        const flightId = route.params.id
        const response = await api.put(`/v1/flights/${flightId}`, flightForm)
        const updatedFlight = response.data
        
        ElMessage.success('Uçuş başarıyla güncellendi')
        
        try {
          console.log('📤 WebSocket ile uçuş güncellemesi gönderiliyor...')
          nativeWebSocketService.sendFlightUpdate('UPDATE', updatedFlight)
          console.log('✅ WebSocket mesajı gönderildi')
        } catch (wsError) {
          console.error('❌ WebSocket mesajı gönderilemedi:', wsError)
        }
        
        router.push('/flights')
      } catch (error) {
        if (error.response?.status === 400) {
          ElMessage.error('Geçersiz veri formatı')
        } else {
          ElMessage.error('Uçuş güncellenirken hata oluştu')
          console.error('Error updating flight:', error)
        }
      } finally {
        submitting.value = false
      }
    }
    
    onMounted(async () => {
      await Promise.all([loadReferenceData(), loadFlight()])
      loading.value = false
    })
    
    return {
      flightForm,
      flightFormRef,
      rules,
      loading,
      submitting,
      airlines,
      aircrafts,
      stations,
      flightTypes,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.flight-edit {
  padding: 20px;
}

.loading {
  padding: 20px;
}
</style>