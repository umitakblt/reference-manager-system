<template>
  <div class="flight-create">
    <el-card>
      <template #header>
        <h2>Yeni Uçuş Oluştur</h2>
      </template>
      
      <el-form 
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
              <el-select v-model="flightForm.aircraftId" placeholder="Uçak seçin" style="width: 100%" :loading="!aircrafts.length">
                <el-option 
                  v-for="aircraft in aircrafts" 
                  :key="aircraft.id" 
                  :label="`${aircraft.name || 'İsimsiz'} - ${aircraft.model || 'Model yok'} (${aircraft.capacity || 0} koltuk)`" 
                  :value="aircraft.id" 
                />
                <el-option v-if="!aircrafts.length" label="Uçak verisi yükleniyor..." value="" disabled />
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
                :disabled-date="disabledArrivalDate"
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
          <el-button type="primary" @click="handleSubmit" :loading="loading" :disabled="loading">
            {{ loading ? 'Oluşturuluyor...' : 'Uçuş Oluştur' }}
          </el-button>
          <el-button @click="$router.push('/flights')" :disabled="loading">
            İptal
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../services/api'
import nativeWebSocketService from '@/services/nativeWebSocketService'

export default {
  name: 'FlightCreate',
  setup() {
    const router = useRouter()
    const flightFormRef = ref()
    const loading = ref(false)
    
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
        { required: true, message: 'Varış zamanı gerekli', trigger: 'change' },
        { 
          validator: (rule, value, callback) => {
            if (value && flightForm.scheduledDeparture) {
              const departure = new Date(flightForm.scheduledDeparture)
              const arrival = new Date(value)
              if (arrival <= departure) {
                callback(new Error('Varış zamanı kalkış zamanından sonra olmalıdır'))
              } else {
                callback()
              }
            } else {
              callback()
            }
          }, 
          trigger: 'change' 
        }
      ],
      flightTypeId: [
        { required: true, message: 'Uçuş tipi gerekli', trigger: 'change' }
      ]
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
        
        console.log('📊 Yüklenen veriler:')
        console.log('Uçaklar:', aircrafts.value)
        console.log('Havayolları:', airlines.value)
        console.log('İstasyonlar:', stations.value)
        console.log('Uçuş tipleri:', flightTypes.value)
      } catch (error) {
        ElMessage.error('Referans veriler yüklenirken hata oluştu')
        console.error('Error loading reference data:', error)
      }
    }
    
    const handleSubmit = async () => {
      try {
        console.log('🔍 Form validasyonu başlıyor...')
        await flightFormRef.value.validate()
        console.log('✅ Form validasyonu başarılı')
        
        loading.value = true
        console.log('🔄 Loading başlatıldı')
        
        console.log('📤 Uçuş oluşturuluyor:', JSON.stringify(flightForm, null, 2))
        
        const response = await api.post('/v1/flights', flightForm)
        console.log('📥 Backend response status:', response.status)
        console.log('📥 Backend response data:', response.data)
        
        const createdFlight = response.data
        console.log('✅ Uçuş oluşturuldu:', createdFlight)
        
        ElMessage.success('Uçuş başarıyla oluşturuldu')
        
        try {
          console.log('📤 WebSocket ile uçuş güncellemesi gönderiliyor...')
          nativeWebSocketService.sendFlightUpdate('CREATE', createdFlight)
          console.log('✅ WebSocket mesajı gönderildi')
        } catch (wsError) {
          console.error('❌ WebSocket mesajı gönderilemedi:', wsError)
        }
        
        console.log('🔄 Loading false yapılıyor...')
        loading.value = false
        console.log('✅ Loading false yapıldı')
        
        console.log('🔄 Yönlendirme başlatılıyor...')
        setTimeout(() => {
          console.log('🔄 Router.push çalıştırılıyor...')
          router.push('/flights')
        }, 500)
        
      } catch (error) {
        console.error('❌ Uçuş oluşturma hatası:', error)
        console.error('❌ Error response:', error.response)
        console.error('❌ Error message:', error.message)
        
        if (error.response?.status === 400) {
          ElMessage.error('Geçersiz veri formatı')
        } else if (error.response?.status === 500) {
          ElMessage.error('Sunucu hatası oluştu')
        } else {
          ElMessage.error('Uçuş oluşturulurken hata oluştu')
        }
        
        console.log('🔄 Loading false yapılıyor (hata durumunda)...')
        loading.value = false
        console.log('✅ Loading false yapıldı (hata durumunda)')
      }
    }
    
    const disabledArrivalDate = (time) => {
      if (flightForm.scheduledDeparture) {
        const departure = new Date(flightForm.scheduledDeparture)
        return time.getTime() < departure.getTime() - (24 * 60 * 60 * 1000)
      }
      return false
    }



    watch(() => flightForm.scheduledDeparture, (newDeparture) => {
      if (newDeparture && flightForm.scheduledArrival) {
        const departure = new Date(newDeparture)
        const arrival = new Date(flightForm.scheduledArrival)
        if (arrival <= departure) {
          const newArrival = new Date(departure.getTime() + 2 * 60 * 60 * 1000)
          flightForm.scheduledArrival = newArrival.toISOString().slice(0, 19).replace('T', ' ')
        }
      }
    })
    
    onMounted(() => {
      loadReferenceData()
    })
    
    return {
      flightForm,
      flightFormRef,
      rules,
      loading,
      airlines,
      aircrafts,
      stations,
      flightTypes,
      handleSubmit,
      disabledArrivalDate
    }
  }
}
</script>

<style scoped>
.flight-create {
  padding: 20px;
}
</style> 