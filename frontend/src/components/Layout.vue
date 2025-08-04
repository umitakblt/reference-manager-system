<template>
  <div class="layout-container">
    <!-- Sidebar -->
    <Sidebar 
      :is-collapse="sidebarCollapsed" 
      @toggle-collapse="handleSidebarToggle"
      class="sidebar"
    />
    
    <!-- Main Content Area -->
    <div class="main-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- Header -->
      <header class="header">
        <div class="header-left">
          <el-button 
            link
            @click="toggleSidebar"
            class="sidebar-toggle"
          >
            <el-icon><Fold v-if="!sidebarCollapsed" /><Expand v-else /></el-icon>
          </el-button>
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>Ana Sayfa</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
        </div>
        
        <div class="header-right">
          <div class="user-info">
            <el-dropdown @command="handleUserCommand">
              <div class="user-profile">
                <el-avatar :size="36" :src="userAvatar" class="user-avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ userName }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    Profil
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    Çıkış Yap
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </header>
      
      <!-- Page Content -->
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, provide } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { logout } from '../services/authService'
import Sidebar from './Sidebar.vue'
import { 
  Fold, 
  Expand, 
  User, 
  ArrowDown, 
  SwitchButton 
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const sidebarCollapsed = ref(false)

// Sidebar durumunu provide et
provide('sidebarCollapsed', sidebarCollapsed)

// Sayfa başlıklarını tanımla
const pageTitles = {
  '/dashboard': 'Dashboard',
  '/flights': 'Uçuş Listesi',
  '/create-flight': 'Yeni Uçuş',
  '/edit-flight': 'Uçuş Düzenle',
  '/flight-archive': 'Uçuş Arşivi',
  '/bulk-upload': 'Toplu Yükleme',
  '/reference-data': 'Referans Veriler',
  '/user-management': 'Kullanıcı Yönetimi',
  '/health': 'Sistem Durumu'
}

const currentPageTitle = computed(() => {
  return pageTitles[route.path] || 'Sayfa'
})

const userName = ref('Umit')
const userAvatar = ref('/images/umit-avatar.png')

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const handleSidebarToggle = (collapsed) => {
  sidebarCollapsed.value = collapsed
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      // Profil sayfasına yönlendir
      router.push('/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          'Çıkış yapmak istediğinizden emin misiniz?',
          'Çıkış Onayı',
          {
            confirmButtonText: 'Evet',
            cancelButtonText: 'İptal',
            type: 'warning'
          }
        )
        
        await logout()
        router.push('/login')
      } catch (error) {
        // Kullanıcı iptal etti
      }
      break
  }
}

onMounted(() => {
  // Kullanıcı bilgilerini sessionStorage'dan al
  const username = sessionStorage.getItem('username')
  if (username) {
    userName.value = username
  }
})
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  flex-shrink: 0;
  z-index: 1000;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
  transition: margin-left 0.3s ease;
  overflow: hidden;
}

.main-content.sidebar-collapsed {
  margin-left: 0;
}

.header {
  height: 64px;
  background: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  z-index: 999;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.sidebar-toggle {
  padding: 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: #f1f5f9;
  transform: scale(1.05);
}

.breadcrumb {
  font-size: 14px;
  color: #64748b;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.user-profile:hover {
  background: rgba(99, 102, 241, 0.1);
  border-color: rgba(99, 102, 241, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
}

.username {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.user-avatar {
  border: 2px solid rgba(99, 102, 241, 0.2);
  transition: all 0.3s ease;
  object-fit: cover;
  border-radius: 50%;
  background: #f8fafc;
}

.user-profile:hover .user-avatar {
  border-color: rgba(99, 102, 241, 0.4);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f8fafc;
}

/* Responsive */
@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }
  
  .page-content {
    padding: 16px;
  }
  
  .username {
    display: none;
  }
  
  .breadcrumb {
    display: none;
  }
}

/* Smooth transitions */
.layout-container * {
  transition: all 0.3s ease;
}

/* Glass morphism effect for header */
.header {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

/* Hover effects */
.user-profile:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.sidebar-toggle:hover {
  transform: scale(1.1);
}
</style> 