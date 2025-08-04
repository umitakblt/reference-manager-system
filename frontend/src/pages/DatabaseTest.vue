<template>
  <div class="database-test-page">
    <el-card>
      <template #header>
        <h2>Veritabanı Test Sayfası</h2>
      </template>
      
      <div class="test-content">
        <el-button type="primary" @click="testDatabase" :loading="loading">
          Veritabanı Bağlantısını Test Et
        </el-button>
        
        <div v-if="dbResult" class="db-result">
          <h3>Veritabanı Sonucu:</h3>
          <pre>{{ dbResult }}</pre>
        </div>
        
        <div v-if="error" class="error-message">
          <h3>Hata:</h3>
          <pre>{{ error }}</pre>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../services/api'

export default {
  name: 'DatabaseTest',
  setup() {
    const dbResult = ref(null)
    const error = ref(null)
    const loading = ref(false)
    
    const testDatabase = async () => {
      loading.value = true
      error.value = null
      dbResult.value = null
      
      try {
        const response = await api.get('/health/database')
        dbResult.value = response.data
        ElMessage.success('Veritabanı bağlantısı başarılı!')
      } catch (err) {
        error.value = err.response?.data || err.message
        ElMessage.error('Veritabanı bağlantısı başarısız!')
        console.error('Database Error:', err)
      } finally {
        loading.value = false
      }
    }
    
    return {
      dbResult,
      error,
      loading,
      testDatabase
    }
  }
}
</script>

<style scoped>
.database-test-page {
  padding: 20px;
}

.test-content {
  text-align: center;
}

.db-result, .error-message {
  margin-top: 20px;
  text-align: left;
}

.db-result pre, .error-message pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}

.error-message pre {
  background-color: #fef0f0;
  color: #f56c6c;
}
</style>
