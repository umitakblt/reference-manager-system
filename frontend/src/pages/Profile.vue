<template>
  <div class="profile-page">
    <!-- Header -->
    <div class="profile-header">
      <h1>Profil</h1>
      <p>Kişisel bilgilerinizi görüntüleyin ve düzenleyin</p>
    </div>

    <!-- Profile Content -->
    <el-row :gutter="24">
      <!-- Profile Card -->
      <el-col :xs="24" :lg="8">
        <div class="profile-card">
          <div class="profile-avatar-section">
            <div class="avatar-container">
              <el-avatar :size="120" :src="userProfile.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="avatar-overlay">
                <el-button type="primary" size="small" @click="changeAvatar">
                  <el-icon><Camera /></el-icon>
                  Fotoğraf Değiştir
                </el-button>
              </div>
            </div>
            <h2>{{ userProfile.fullName }}</h2>
            <p class="user-role">{{ userProfile.role }}</p>
            <p class="user-email">{{ userProfile.email }}</p>
          </div>
          
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-number">{{ userProfile.loginCount }}</div>
              <div class="stat-label">Giriş Sayısı</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ userProfile.lastLogin }}</div>
              <div class="stat-label">Son Giriş</div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- Profile Form -->
      <el-col :xs="24" :lg="16">
        <div class="profile-form-card">
          <div class="card-header">
            <h3>Kişisel Bilgiler</h3>
            <el-button type="primary" @click="saveProfile" :loading="saving">
              <el-icon><Check /></el-icon>
              Kaydet
            </el-button>
          </div>

          <el-form 
            ref="profileForm" 
            :model="profileForm" 
            :rules="formRules" 
            label-width="120px"
            class="profile-form"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="Ad" prop="firstName">
                  <el-input 
                    v-model="profileForm.firstName" 
                    placeholder="Adınız"
                    clearable
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="Soyad" prop="lastName">
                  <el-input 
                    v-model="profileForm.lastName" 
                    placeholder="Soyadınız"
                    clearable
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="E-posta" prop="email">
              <el-input 
                v-model="profileForm.email" 
                placeholder="E-posta adresiniz"
                clearable
                disabled
              />
            </el-form-item>

            <el-form-item label="Telefon" prop="phone">
              <el-input 
                v-model="profileForm.phone" 
                placeholder="Telefon numaranız"
                clearable
              />
            </el-form-item>

            <el-form-item label="Pozisyon" prop="position">
              <el-input 
                v-model="profileForm.position" 
                placeholder="Pozisyonunuz"
                clearable
              />
            </el-form-item>

            <el-form-item label="Departman" prop="department">
              <el-select 
                v-model="profileForm.department" 
                placeholder="Departman seçin"
                clearable
                style="width: 100%"
              >
                <el-option label="IT" value="IT" />
                <el-option label="Operasyon" value="Operations" />
                <el-option label="Yönetim" value="Management" />
                <el-option label="Müşteri Hizmetleri" value="Customer Service" />
              </el-select>
            </el-form-item>

            <el-form-item label="Adres" prop="address">
              <el-input 
                v-model="profileForm.address" 
                type="textarea" 
                :rows="3"
                placeholder="Adresiniz"
                clearable
              />
            </el-form-item>
          </el-form>
        </div>

        <!-- Security Section -->
        <div class="security-card">
          <div class="card-header">
            <h3>Güvenlik</h3>
          </div>
          
          <div class="security-options">
            <div class="security-item">
              <div class="security-info">
                <h4>Şifre Değiştir</h4>
                <p>Hesabınızın güvenliği için şifrenizi güncelleyin</p>
              </div>
              <el-button type="primary" @click="showChangePassword = true">
                Değiştir
              </el-button>
            </div>
            
            <div class="security-item">
              <div class="security-info">
                <h4>İki Faktörlü Doğrulama</h4>
                <p>Hesabınızı daha güvenli hale getirin</p>
              </div>
              <el-switch v-model="twoFactorEnabled" />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Change Password Dialog -->
    <el-dialog 
      v-model="showChangePassword" 
      title="Şifre Değiştir" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form 
        ref="passwordForm" 
        :model="passwordForm" 
        :rules="passwordRules" 
        label-width="140px"
      >
        <el-form-item label="Mevcut Şifre" prop="currentPassword">
          <el-input 
            v-model="passwordForm.currentPassword" 
            type="password" 
            placeholder="Mevcut şifrenizi girin"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="Yeni Şifre" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            placeholder="Yeni şifrenizi girin"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="Şifre Tekrar" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            placeholder="Yeni şifrenizi tekrar girin"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showChangePassword = false">İptal</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">
          Şifreyi Değiştir
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  User, 
  Camera, 
  Check 
} from '@element-plus/icons-vue'

// Reactive data
const profileForm = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  position: '',
  department: '',
  address: ''
})

const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const userProfile = ref({
  fullName: 'Umit Akbulut',
  email: 'umit@example.com',
  role: 'Sistem Yöneticisi',
  avatar: '',
  loginCount: 127,
  lastLogin: '2 saat önce'
})

const saving = ref(false)
const changingPassword = ref(false)
const showChangePassword = ref(false)
const twoFactorEnabled = ref(false)

// Form validation rules
const formRules = {
  firstName: [
    { required: true, message: 'Ad alanı zorunludur', trigger: 'blur' },
    { min: 2, message: 'Ad en az 2 karakter olmalıdır', trigger: 'blur' }
  ],
  lastName: [
    { required: true, message: 'Soyad alanı zorunludur', trigger: 'blur' },
    { min: 2, message: 'Soyad en az 2 karakter olmalıdır', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'E-posta alanı zorunludur', trigger: 'blur' },
    { type: 'email', message: 'Geçerli bir e-posta adresi girin', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^[0-9+\-\s()]+$/, message: 'Geçerli bir telefon numarası girin', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: 'Mevcut şifre zorunludur', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: 'Yeni şifre zorunludur', trigger: 'blur' },
    { min: 8, message: 'Şifre en az 8 karakter olmalıdır', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: 'Şifre tekrarı zorunludur', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('Şifreler eşleşmiyor'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// Methods
const loadUserProfile = () => {
  // Simüle edilmiş kullanıcı verileri
  profileForm.firstName = 'Umit'
  profileForm.lastName = 'Akbulut'
  profileForm.email = 'umit@example.com'
  profileForm.phone = '+90 555 123 4567'
  profileForm.position = 'Sistem Yöneticisi'
  profileForm.department = 'IT'
  profileForm.address = 'İstanbul, Türkiye'
}

const saveProfile = async () => {
  try {
    saving.value = true
    
    // Form validasyonu
    const valid = await profileForm.value?.validate()
    if (!valid) return
    
    // API çağrısı simülasyonu
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('Profil başarıyla güncellendi')
  } catch (error) {
    ElMessage.error('Profil güncellenirken hata oluştu')
  } finally {
    saving.value = false
  }
}

const changeAvatar = () => {
  ElMessage.info('Fotoğraf değiştirme özelliği yakında eklenecek')
}

const changePassword = async () => {
  try {
    changingPassword.value = true
    
    // Form validasyonu
    const valid = await passwordForm.value?.validate()
    if (!valid) return
    
    // API çağrısı simülasyonu
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('Şifre başarıyla değiştirildi')
    showChangePassword.value = false
    
    // Form temizleme
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    ElMessage.error('Şifre değiştirilirken hata oluştu')
  } finally {
    changingPassword.value = false
  }
}

onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped>
.profile-page {
  padding: 24px;
}

.profile-header {
  margin-bottom: 32px;
  text-align: center;
}

.profile-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.profile-header p {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

.profile-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px;
  margin-bottom: 24px;
  text-align: center;
}

.profile-avatar-section {
  margin-bottom: 32px;
}

.avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.profile-card h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.user-role {
  font-size: 14px;
  color: #6366f1;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.user-email {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.profile-form-card,
.security-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  padding: 32px;
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e2e8f0;
}

.card-header h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.profile-form {
  margin-top: 20px;
}

.security-options {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
}

.security-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.security-info p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }
  
  .profile-card,
  .profile-form-card,
  .security-card {
    padding: 24px;
  }
  
  .card-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .security-item {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .profile-page {
    padding: 12px;
  }
  
  .profile-header h1 {
    font-size: 24px;
  }
  
  .profile-card,
  .profile-form-card,
  .security-card {
    padding: 20px;
  }
  
  .profile-card h2 {
    font-size: 20px;
  }
  
  .stat-number {
    font-size: 20px;
  }
}
</style> 