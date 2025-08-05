<template>
  <div class="container">
    <h2>Uçuş Ekle</h2>
    <form @submit.prevent="submitFlight" class="flight-form">
      <input v-model="form.flightNumber" placeholder="Flight Number" required />
      <select v-model="form.airlineId" required>
        <option value="" disabled selected>Havayolu Seç</option>
        <option v-for="airline in airlines" :key="airline.id" :value="airline.id">{{ airline.name }}</option>
      </select>
      <select v-model="form.aircraftId" required>
        <option value="" disabled selected>Uçak Seç</option>
        <option v-for="aircraft in aircrafts" :key="aircraft.id" :value="aircraft.id">{{ aircraft.name }}</option>
      </select>
      <select v-model="form.originStationId" required>
        <option value="" disabled selected>Kalkış İstasyonu Seç</option>
        <option v-for="station in stations" :key="station.id" :value="station.id">{{ station.name }}</option>
      </select>
      <select v-model="form.destinationStationId" required>
        <option value="" disabled selected>Varış İstasyonu Seç</option>
        <option v-for="station in stations" :key="station.id" :value="station.id">{{ station.name }}</option>
      </select>
      <input v-model="form.flightTypeId" placeholder="Flight Type ID" required />
      <input v-model="form.scheduledDeparture" placeholder="Departure (YYYY-MM-DDTHH:MM)" required />
      <input v-model="form.scheduledArrival" placeholder="Arrival (YYYY-MM-DDTHH:MM)" required />
      <button type="submit">Kaydet</button>
    </form>
    <!-- Formun hemen altına, seçilen değerlerin özetini gösteren bir alan ekliyorum -->
    <div v-if="form.airlineId || form.aircraftId || form.originStationId || form.destinationStationId" class="form-summary">
      <p><b>Seçilen Havayolu:</b> {{ getAirlineName(form.airlineId, airlines) }}</p>
      <p><b>Seçilen Uçak:</b> {{ getAircraftName(form.aircraftId, aircrafts) }}</p>
      <p><b>Kalkış İstasyonu:</b> {{ getStationName(form.originStationId, stations) }}</p>
      <p><b>Varış İstasyonu:</b> {{ getStationName(form.destinationStationId, stations) }}</p>
    </div>
    <p v-if="successMessage">{{ successMessage }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../services/api'
import { useRouter } from 'vue-router'
import { getAirlineName, getAircraftName, getStationName } from '../utils/lookup'

const router = useRouter()
const form = ref({
  flightNumber: '',
  airlineId: '',
  aircraftId: '',
  originStationId: '',
  destinationStationId: '',
  flightTypeId: '',
  scheduledDeparture: '',
  scheduledArrival: ''
})
const successMessage = ref('')
const errorMessage = ref('')

const airlines = ref([])
const aircrafts = ref([])
const stations = ref([])



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
  } catch (err) {
  }
}

onMounted(() => {
  loadReferenceData()
})

const submitFlight = async () => {
  try {
    await api.post('/v1/flights', form.value)

    successMessage.value = 'Uçuş başarıyla eklendi!'
    errorMessage.value = ''
    router.push('/')
  } catch (error) {
    console.error('Uçuş eklenirken hata oluştu:', error)
    errorMessage.value = 'Uçuş eklenirken hata oluştu.'
    successMessage.value = ''
  }
}
</script>

<style scoped>
/* Basit form stilleri */
</style>
