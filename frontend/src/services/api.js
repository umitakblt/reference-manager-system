import axios from 'axios'
import { getToken, logout } from './authService'
import router from '../router'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: 'http://localhost:8083/api'
})

api.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      logout()
      router.push('/login')
    }
    const msg = error.response?.data?.message || error.message || 'Bilinmeyen bir hata oluştu'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default api
