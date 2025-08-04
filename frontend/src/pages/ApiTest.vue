<template>
  <div class="api-test">
    <el-card>
      <template #header>
        <h2>API Test Sayfası</h2>
      </template>
      
      <div class="test-buttons">
        <el-button @click="testEndpoint('/flights')" type="primary">
          Test Flights
        </el-button>
        <el-button @click="testEndpoint('/airlines')" type="success">
          Test Airlines
        </el-button>
        <el-button @click="testEndpoint('/aircrafts')" type="warning">
          Test Aircrafts
        </el-button>
        <el-button @click="testEndpoint('/stations')" type="info">
          Test Stations
        </el-button>
        <el-button @click="testEndpoint('/flight-types')" type="danger">
          Test Flight Types
        </el-button>
        <el-button @click="testEndpoint('/flights/archive')" type="primary">
          Test Archive
        </el-button>
      </div>
      
      <div v-if="testResult" class="test-result">
        <h3>Test Sonucu:</h3>
        <pre>{{ testResult }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../services/api'

export default {
  name: 'ApiTest',
  setup() {
    const testResult = ref(null)
    
    const testEndpoint = async (endpoint) => {
      try {
        const response = await api.get(endpoint)
        testResult.value = {
          endpoint,
          status: response.status,
          data: response.data
        }
        ElMessage.success(`${endpoint} başarılı!`)
      } catch (error) {
        testResult.value = {
          endpoint,
          error: error.response?.data || error.message,
          status: error.response?.status
        }
        ElMessage.error(`${endpoint} hatası!`)
      }
    }
    
    return {
      testResult,
      testEndpoint
    }
  }
}
</script>

<style scoped>
.api-test {
  padding: 20px;
}

.test-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.test-result {
  margin-top: 20px;
  text-align: left;
}

.test-result pre {
  background-color: #f5f5f5;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}
</style> 