import api from './api'

export const flightService = {
  getAllFlights: () => api.get('/api/v1/flights'),
  getFlightById: (id) => api.get(`/api/v1/flights/${id}`),
  createFlight: (flight) => api.post('/api/v1/flights', flight),
  updateFlight: (id, flight) => api.put(`/api/v1/flights/${id}`, flight),
  deleteFlight: (id) => api.delete(`/api/v1/flights/${id}`),
  addFlight: (flight) => api.post('/api/v1/flights', flight)
}
