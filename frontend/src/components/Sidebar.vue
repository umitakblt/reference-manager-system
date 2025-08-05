<template>
  <div class="sidebar-container" :style="{ width: isCollapse ? '80px' : sidebarWidth + 'px' }">
    <!-- Resize handle -->
    <div 
      class="resize-handle" 
      @mousedown="startResize"
      @touchstart="startResize"
      @dblclick="toggleFullClose"
      title="Sürükle: Genişliği ayarla | Çift tık: Tamamen kapat/aç"
      :class="{ 'handle-closed': sidebarWidth === 0 }"
    ></div>
    
    <el-menu
      :default-active="activeIndex"
      class="sidebar-menu"
      :collapse="isCollapse"
      background-color="transparent"
      text-color="#64748b"
      active-text-color="#6366f1"
      router
    >
      <div class="sidebar-header">
        <div class="logo-container" @click="goToDashboard" style="cursor: pointer;">
          <div class="airplane-icon">
            <el-icon><Promotion /></el-icon>
          </div>
          <span v-if="!isCollapse" class="app-title">FlightMS</span>
        </div>
        <div class="app-subtitle" v-if="!isCollapse">
          Uçuş Yönetim Sistemi
        </div>
      </div>

      <div class="menu-section">
        <div class="section-title" v-if="!isCollapse">Ana Menü</div>
        
        <el-menu-item 
          v-for="item in menuItems.filter(item => !item.adminOnly)"
          :key="item.index"
          :index="item.index" 
          class="menu-item"
        >
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </div>

      <div class="menu-section" v-if="menuItems.some(item => item.adminOnly)">
        <div class="section-title" v-if="!isCollapse">Yönetim</div>
        
        <el-menu-item 
          v-for="item in menuItems.filter(item => item.adminOnly)"
          :key="item.index"
          :index="item.index" 
          class="menu-item"
        >
          <el-icon>
            <component :is="item.icon" />
          </el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </div>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  House, 
  Location, 
  Monitor, 
  Plus, 
  Document, 
  Upload, 
  Promotion, 
  Clock, 
  Close, 
  Refresh, 
  Search, 
  Download, 
  Right,
  User,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'
import { canPerformAction, isAdmin, isUser } from '../services/authService'

const route = useRoute()
const router = useRouter()

const goToDashboard = () => {
  router.push('/dashboard')
}

const props = defineProps({
  isCollapse: {
    type: Boolean,
    default: false
  }
})

const activeIndex = computed(() => route.path)

const emit = defineEmits(['toggle-collapse'])

const sidebarWidth = ref(280)
const isResizing = ref(false)
const startX = ref(0)
const startWidth = ref(0)

watch(() => props.isCollapse, (newValue) => {
  emit('toggle-collapse', newValue)
  if (newValue && sidebarWidth.value > 0) {
    sidebarWidth.value = 80
  } else if (!newValue && sidebarWidth.value === 80) {
    sidebarWidth.value = 280
  }
})

const startResize = (event) => {
  event.preventDefault()
  isResizing.value = true
  startX.value = event.type === 'mousedown' ? event.clientX : event.touches[0].clientX
  startWidth.value = sidebarWidth.value
  
  document.addEventListener('mousemove', handleResize)
  document.addEventListener('mouseup', stopResize)
  document.addEventListener('touchmove', handleResize)
  document.addEventListener('touchend', stopResize)
}

const handleResize = (event) => {
  if (!isResizing.value) return
  
  const currentX = event.type === 'mousemove' ? event.clientX : event.touches[0].clientX
  const deltaX = currentX - startX.value
  const newWidth = Math.max(0, Math.min(400, startWidth.value + deltaX))
  
  sidebarWidth.value = newWidth
}

const stopResize = () => {
  isResizing.value = false
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.removeEventListener('touchmove', handleResize)
  document.removeEventListener('touchend', stopResize)
}

const toggleFullClose = () => {
  if (sidebarWidth.value > 0) {
    sidebarWidth.value = 0
  } else {
    sidebarWidth.value = 280
  }
}

onUnmounted(() => {
  document.removeEventListener('mousemove', handleResize)
  document.removeEventListener('mouseup', stopResize)
  document.removeEventListener('touchmove', handleResize)
  document.removeEventListener('touchend', stopResize)
})

    const menuItems = computed(() => {
      const items = [
        {
          index: '/dashboard',
          title: 'Dashboard',
          icon: House,
          roles: ['ADMIN', 'USER'],
          adminOnly: false
        },
        {
          index: '/flights',
          title: 'Uçuş Listesi',
          icon: Location,
          roles: ['ADMIN', 'USER'],
          adminOnly: false
        },
        {
          index: '/create-flight',
          title: 'Yeni Uçuş',
          icon: Plus,
          roles: ['ADMIN'],
          action: 'create-flight',
          adminOnly: true
        },
        {
          index: '/flight-archive',
          title: 'Uçuş Arşivi',
          icon: Document,
          roles: ['ADMIN'],
          action: 'view-archive',
          adminOnly: true
        },
        {
          index: '/bulk-upload',
          title: 'Toplu Yükleme',
          icon: Upload,
          roles: ['ADMIN'],
          action: 'bulk-upload',
          adminOnly: true
        },
        {
          index: '/reference-data',
          title: 'Referans Veriler',
          icon: Setting,
          roles: ['ADMIN'],
          adminOnly: true
        },
        {
          index: '/user-management',
          title: 'Kullanıcı Yönetimi',
          icon: User,
          roles: ['ADMIN'],
          action: 'manage-users',
          adminOnly: true
        },
        {
          index: '/health',
          title: 'Sistem Durumu',
          icon: Monitor,
          roles: ['ADMIN'],
          adminOnly: true
        }
      ]
      
      return items.filter(item => {
        const hasRole = item.roles.some(role => {
          if (role === 'ADMIN') return isAdmin()
          if (role === 'USER') return isUser()
          return false
        })
        
        const hasAction = !item.action || canPerformAction(item.action)
        
        return hasRole && hasAction
      })
    })

</script>

<style scoped>
.sidebar-container {
  height: 100vh;
  background: linear-gradient(180deg, #1e293b 0%, #334155 100%);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
  transition: width 0.1s ease;
}

.resize-handle {
  position: absolute;
  top: 0;
  right: -3px;
  width: 6px;
  height: 100%;
  cursor: col-resize;
  background: transparent;
  z-index: 1000;
  transition: background 0.3s ease;
}

/* Sidebar kapandığında resize handle'ı görünür yap */
.handle-closed {
  right: 0 !important;
  width: 12px !important;
  background: rgba(99, 102, 241, 0.4) !important;
  border-radius: 0 4px 4px 0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.2);
}

.handle-closed:hover {
  background: rgba(99, 102, 241, 0.6) !important;
  width: 16px !important;
}

.resize-handle:hover {
  background: rgba(99, 102, 241, 0.3);
}

.resize-handle:active {
  background: rgba(99, 102, 241, 0.5);
}

.sidebar-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);
  pointer-events: none;
}

.sidebar-menu {
  height: 100%;
  border: none;
  background: transparent;
  padding: 0;
}

.sidebar-header {
  padding: 24px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 20px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
  border-radius: 12px;
  padding: 8px;
}

.logo-container:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.airplane-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.airplane-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3) 0%, rgba(255, 255, 255, 0.1) 100%);
  border-radius: 14px;
}

.airplane-icon .el-icon {
  font-size: 22px;
  color: #ffffff;
  position: relative;
  z-index: 2;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
  transition: all 0.3s ease;
}

.airplane-icon:hover {
  transform: scale(1.15) rotate(8deg);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.5);
  border-color: rgba(255, 255, 255, 0.2);
}

.airplane-icon:hover .el-icon {
  transform: scale(1.1);
}



.app-title {
  font-size: 22px;
  font-weight: 800;
  color: #ffffff;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #ffffff 0%, #e2e8f0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  position: relative;
}

.app-title::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  border-radius: 1px;
}

.app-subtitle {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  text-transform: uppercase;
  margin-top: 4px;
}

.menu-section {
  margin-bottom: 24px;
}

.section-title {
  padding: 0 20px 8px;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 8px;
}

.menu-item {
  margin: 4px 12px;
  border-radius: 12px;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  color: #64748b !important;
  font-weight: 500;
  font-size: 14px;
  z-index: 1;
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 12px;
  z-index: -1;
}

.menu-item:hover::before {
  opacity: 1;
}

.menu-item:hover {
  transform: translateX(8px);
  color: #ffffff !important;
}

.menu-item.is-active {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: #ffffff !important;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
  transform: translateX(8px);
  font-weight: 600;
}

.menu-item .el-icon {
  margin-right: 12px;
  font-size: 18px;
  color: inherit;
  position: relative;
  z-index: 2;
}

/* Element Plus menu item title'ının z-index'ini artır */
.menu-item .el-menu-item__title {
  position: relative;
  z-index: 10 !important;
  color: inherit !important;
  font-weight: 500 !important;
  font-size: 14px !important;
}

/* Hover durumunda title'ın rengini koru */
.menu-item:hover .el-menu-item__title {
  color: #ffffff !important;
  font-weight: 500 !important;
}

.menu-item.is-active .el-menu-item__title {
  color: #ffffff !important;
  font-weight: 600 !important;
}

/* Element Plus'ın kendi hover efektini devre dışı bırak */
.menu-item:hover {
  background: transparent !important;
}

.menu-item.is-active {
  background: transparent !important;
}



/* Collapse styles */
.sidebar-menu.el-menu--collapse {
  width: 80px;
}

.sidebar-menu.el-menu--collapse .sidebar-header {
  padding: 20px 12px;
  text-align: center;
}

.sidebar-menu.el-menu--collapse .logo-container {
  justify-content: center;
  margin-bottom: 0;
}

.sidebar-menu.el-menu--collapse .airplane-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
}

.sidebar-menu.el-menu--collapse .airplane-icon .el-icon {
  font-size: 20px;
}

.sidebar-menu.el-menu--collapse .resize-handle {
  display: none;
}

.sidebar-menu.el-menu--collapse .app-subtitle {
  display: none;
}

.sidebar-menu.el-menu--collapse .section-title {
  display: none;
}

.sidebar-menu.el-menu--collapse .menu-item {
  margin: 4px 8px;
  text-align: center;
  color: #94a3b8 !important;
}

.sidebar-menu.el-menu--collapse .menu-item:hover {
  color: #ffffff !important;
}

.sidebar-menu.el-menu--collapse .menu-item.is-active {
  color: #ffffff !important;
}

.sidebar-menu.el-menu--collapse .menu-item .el-icon {
  margin-right: 0;
  font-size: 20px;
}



/* Responsive */
@media (max-width: 768px) {
  .sidebar-container {
    width: 80px !important;
  }
  
  .resize-handle {
    display: none;
  }
  
  .app-title,
  .app-subtitle,
  .section-title {
    display: none;
  }
  
  .airplane-icon {
    width: 36px;
    height: 36px;
    border-radius: 10px;
  }
  
  .airplane-icon .el-icon {
    font-size: 18px;
  }
  
  .menu-item {
    margin: 4px 8px;
    text-align: center;
  }
  
  .menu-item .el-icon {
    margin-right: 0;
  }
}

/* Animation for menu items */
.menu-item {
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Glass morphism effect */
.sidebar-container {
  background: rgba(30, 41, 59, 0.95);
  backdrop-filter: blur(20px);
  border-right: 1px solid rgba(255, 255, 255, 0.1);
}

/* Hover effects */
.menu-item:hover {
  transform: translateX(8px) scale(1.02);
}

.menu-item.is-active {
  transform: translateX(8px) scale(1.02);
}

/* Icon animations */
.menu-item .el-icon {
  transition: transform 0.3s ease;
}

.menu-item:hover .el-icon {
  transform: scale(1.1);
}

.menu-item.is-active .el-icon {
  transform: scale(1.1);
}
</style> 