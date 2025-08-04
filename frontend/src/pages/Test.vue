<template>
  <div class="test-page">
    <el-card class="test-card">
      <template #header>
        <h2>Test Sayfası</h2>
      </template>
      
      <div class="test-content">
        <p>Bu sayfa login olmadan erişilebilir.</p>
        
        <el-button type="primary" @click="goToDashboard">
          Dashboard'a Git
        </el-button>
        
        <el-button type="success" @click="goToLogin">
          Login Sayfasına Git
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'Test',
  setup() {
    const router = useRouter()
    
    const goToDashboard = () => {
      router.push('/dashboard')
    }
    
    const goToLogin = () => {
      router.push('/login')
    }
    
    return {
      goToDashboard,
      goToLogin
    }
  }
}
</script>

<style scoped>
.test-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #4f46e5 100%);
}

.test-card {
  width: 400px;
  text-align: center;
}

.test-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
}

.test-content p {
  margin: 0;
  color: #64748b;
}
</style> 