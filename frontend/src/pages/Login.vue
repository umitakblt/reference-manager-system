<template>
  <div class="login-container">

    <div class="animated-background">

      <div class="airplane airplane-1">✈️</div>
      <div class="airplane airplane-2">✈️</div>
      <div class="airplane airplane-3">✈️</div>
      <div class="airplane airplane-4">✈️</div>
      
      <div class="firework firework-1">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-2">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-3">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-4">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-5">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-6">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-7">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      <div class="firework firework-8">
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
        <div class="spark"></div>
      </div>
      
      
      <div class="cloud cloud-1">☁️</div>
      <div class="cloud cloud-2">☁️</div>
      <div class="cloud cloud-3">☁️</div>
      
      
      <div class="background-overlay"></div>
    </div>
    
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>Uçuş Yönetim Sistemi</h2>
          <p>Giriş yaparak sisteme erişim sağlayın</p>
        </div>
      </template>
      
      <el-form 
        :model="loginForm" 
        :rules="rules" 
        ref="loginFormRef" 
        label-width="80px"
        @submit.prevent="handleLogin"
        class="login-form"
      >
        <el-form-item label="Kullanıcı Adı" prop="username">
          <el-input 
            v-model="loginForm.username" 
            placeholder="Kullanıcı adınızı girin"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        
        <el-form-item label="Şifre" prop="password">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="Şifrenizi girin"
            prefix-icon="Lock"
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading"
            size="large"
            class="login-button"
          >
            Giriş Yap
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../services/authService'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref()
    const loading = ref(false)
    
    const loginForm = reactive({
      username: '',
      password: ''
    })
    
    const rules = {
      username: [
        { required: true, message: 'Kullanıcı adı gerekli', trigger: 'blur' }
      ],
      password: [
        { required: true, message: 'Şifre gerekli', trigger: 'blur' }
      ]
    }
    
    const handleLogin = async () => {
      try {
        await loginFormRef.value.validate()
        loading.value = true
        
        const response = await login(loginForm)
        sessionStorage.setItem('jwt', response.data.token)
        sessionStorage.setItem('user', JSON.stringify(response.data.user))
        
        ElMessage.success('Başarıyla giriş yapıldı')
        router.push('/dashboard')
      } catch (error) {
        if (error.response?.status === 401) {
          ElMessage.error('Kullanıcı adı veya şifre hatalı')
        } else {
          ElMessage.error('Giriş yapılırken hata oluştu')
          console.error('Login error:', error)
        }
      } finally {
        loading.value = false
      }
    }
    
    return {
      loginForm,
      loginFormRef,
      rules,
      loading,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #667eea 100%);
  overflow: hidden;
}

.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.airplane {
  position: absolute;
  font-size: 2rem;
  animation: fly 15s linear infinite;
  opacity: 0.8;
  filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.3));
}

.airplane-1 {
  top: 20%;
  animation-delay: 0s;
  animation-duration: 20s;
}

.airplane-2 {
  top: 40%;
  animation-delay: 5s;
  animation-duration: 18s;
}

.airplane-3 {
  top: 60%;
  animation-delay: 10s;
  animation-duration: 22s;
}

.airplane-4 {
  top: 80%;
  animation-delay: 15s;
  animation-duration: 25s;
}

@keyframes fly {
  0% {
    transform: translateX(-100px) rotate(15deg);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateX(calc(100vw + 100px)) rotate(15deg);
    opacity: 0;
  }
}

.firework {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  z-index: 5;
}

.firework-1 {
  top: 30%;
  left: 20%;
  animation: firework1 4s ease-in-out infinite;
}

.firework-2 {
  top: 50%;
  left: 70%;
  animation: firework2 6s ease-in-out infinite;
  animation-delay: 2s;
}

.firework-3 {
  top: 70%;
  left: 40%;
  animation: firework3 5s ease-in-out infinite;
  animation-delay: 4s;
}

.firework-4 {
  top: 25%;
  left: 80%;
  animation: firework4 7s ease-in-out infinite;
  animation-delay: 1s;
}

.firework-5 {
  top: 85%;
  left: 15%;
  animation: firework5 6s ease-in-out infinite;
  animation-delay: 3s;
}

.firework-6 {
  top: 35%;
  left: 60%;
  animation: firework6 8s ease-in-out infinite;
  animation-delay: 5s;
}

.firework-7 {
  top: 65%;
  left: 85%;
  animation: firework7 4s ease-in-out infinite;
  animation-delay: 2s;
}

.firework-8 {
  top: 15%;
  left: 35%;
  animation: firework8 9s ease-in-out infinite;
  animation-delay: 6s;
}

.spark {
  position: absolute;
  width: 4px;
  height: 4px;
  background: radial-gradient(circle, #ff6b6b, #ffd93d, #6bcf7f, #4d9de0, #ff8e53, #ff6b9d);
  border-radius: 50%;
  animation: spark 2s ease-out infinite;
  box-shadow: 0 0 8px currentColor;
  z-index: 6;
}

.firework-1 .spark { background: radial-gradient(circle, #ff6b6b, #ffd93d, #6bcf7f); }
.firework-2 .spark { background: radial-gradient(circle, #4d9de0, #ff8e53, #ff6b9d); }
.firework-3 .spark { background: radial-gradient(circle, #6bcf7f, #ffd93d, #ff6b6b); }
.firework-4 .spark { background: radial-gradient(circle, #ff8e53, #4d9de0, #ff6b9d); }
.firework-5 .spark { background: radial-gradient(circle, #ffd93d, #6bcf7f, #4d9de0); }
.firework-6 .spark { background: radial-gradient(circle, #ff6b9d, #ff8e53, #ff6b6b); }
.firework-7 .spark { background: radial-gradient(circle, #4d9de0, #6bcf7f, #ffd93d); }
.firework-8 .spark { background: radial-gradient(circle, #ff6b6b, #ff8e53, #6bcf7f); }

.spark:nth-child(1) { transform: rotate(0deg) translateY(-30px); animation-delay: 0s; }
.spark:nth-child(2) { transform: rotate(30deg) translateY(-30px); animation-delay: 0.1s; }
.spark:nth-child(3) { transform: rotate(60deg) translateY(-30px); animation-delay: 0.2s; }
.spark:nth-child(4) { transform: rotate(90deg) translateY(-30px); animation-delay: 0.3s; }
.spark:nth-child(5) { transform: rotate(120deg) translateY(-30px); animation-delay: 0.4s; }
.spark:nth-child(6) { transform: rotate(150deg) translateY(-30px); animation-delay: 0.5s; }
.spark:nth-child(7) { transform: rotate(180deg) translateY(-30px); animation-delay: 0.6s; }
.spark:nth-child(8) { transform: rotate(210deg) translateY(-30px); animation-delay: 0.7s; }
.spark:nth-child(9) { transform: rotate(240deg) translateY(-30px); animation-delay: 0.8s; }
.spark:nth-child(10) { transform: rotate(270deg) translateY(-30px); animation-delay: 0.9s; }
.spark:nth-child(11) { transform: rotate(300deg) translateY(-30px); animation-delay: 1s; }
.spark:nth-child(12) { transform: rotate(330deg) translateY(-30px); animation-delay: 1.1s; }

@keyframes firework1 {
  0%, 100% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.5); opacity: 1; }
}

@keyframes firework2 {
  0%, 100% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.8); opacity: 1; }
}

@keyframes firework3 {
  0%, 100% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.3); opacity: 1; }
}

@keyframes firework4 {
  0%, 100% { transform: scale(0) rotate(0deg); opacity: 0; }
  50% { transform: scale(1.5) rotate(180deg); opacity: 1; }
}

@keyframes firework5 {
  0%, 100% { transform: scale(0); opacity: 0; }
  30% { transform: scale(0.5); opacity: 0.5; }
  70% { transform: scale(1.3); opacity: 1; }
}

@keyframes firework6 {
  0%, 100% { transform: scale(0) translateY(0); opacity: 0; }
  50% { transform: scale(1.2) translateY(-20px); opacity: 1; }
}

@keyframes firework7 {
  0%, 100% { transform: scale(0) rotate(0deg); opacity: 0; }
  25% { transform: scale(0.7) rotate(90deg); opacity: 0.7; }
  75% { transform: scale(1.4) rotate(270deg); opacity: 1; }
}

@keyframes firework8 {
  0%, 100% { transform: scale(0); opacity: 0; }
  40% { transform: scale(0.9); opacity: 0.8; }
  80% { transform: scale(1.1); opacity: 1; }
}

@keyframes spark {
  0% {
    transform: translateY(0) scale(1);
    opacity: 1;
  }
  50% {
    transform: translateY(-30px) scale(1.5);
    opacity: 0.9;
  }
  100% {
    transform: translateY(-60px) scale(0);
    opacity: 0;
  }
}

/* Bulut Animasyonları */
.cloud {
  position: absolute;
  font-size: 3rem;
  opacity: 0.6;
  animation: float 20s ease-in-out infinite;
}

.cloud-1 {
  top: 15%;
  animation-delay: 0s;
  animation-duration: 25s;
}

.cloud-2 {
  top: 45%;
  animation-delay: 8s;
  animation-duration: 30s;
}

.cloud-3 {
  top: 75%;
  animation-delay: 15s;
  animation-duration: 22s;
}

@keyframes float {
  0%, 100% {
    transform: translateX(-100px);
    opacity: 0;
  }
  20%, 80% {
    opacity: 0.6;
  }
  50% {
    transform: translateX(calc(100vw + 100px));
    opacity: 0.6;
  }
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(30, 60, 114, 0.7) 0%, rgba(42, 82, 152, 0.5) 50%, rgba(102, 126, 234, 0.3) 100%);
  z-index: 1;
}

.login-card {
  width: 450px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  padding: 20px 0;
}

.login-header h2 {
  margin: 0 0 8px 0;
  color: #409EFF;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  opacity: 0.8;
}

.login-form {
  padding: 20px 0;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #409EFF, #67C23A);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.login-button:active {
  transform: translateY(0);
}

/* Responsive tasarım */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 400px;
    margin: 20px;
  }
  
  .login-header h2 {
    font-size: 24px;
  }
}
</style> 