import { defineStore } from 'pinia'
import { getAllFlights, createFlight } from '../services/flightService'

export const useFlightStore = defineStore('flightStore', {
  state: () => ({
    flights: [],
    loading: false,
    error: null
  }),
  actions: {
    async fetchFlights() {
      this.loading = true
      try {
        const response = await getAllFlights()
        this.flights = response.data
      } catch (e) {
        this.error = 'Uçuşlar alınamadı'
        console.error('Fetch error:', e)
      } finally {
        this.loading = false
      }
    },
    async addFlight(flight) {
      try {
        await createFlight(flight)
      } catch (e) {
        this.error = 'Uçuş eklenemedi'
        console.error('Add error:', e)
        throw e
      }
    }
  }
})
