import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'
import Login from '../pages/Login.vue'
import Dashboard from '../pages/Dashboard.vue'
import Test from '../pages/Test.vue'
import DatabaseTest from '../pages/DatabaseTest.vue'
import ApiTest from '../pages/ApiTest.vue'
import WebSocketTest from '../pages/WebSocketTest.vue'
import NativeWebSocketTest from '../pages/NativeWebSocketTest.vue'

import FlightList from '../views/FlightList.vue'
import FlightAdd from '../views/FlightAdd.vue'
import FlightCreate from '../pages/FlightCreate.vue'
import FlightEdit from '../pages/FlightEdit.vue'
import FlightArchive from '../pages/FlightArchive.vue'
import FlightBulkUpload from '../pages/FlightBulkUpload.vue'
import ReferenceData from '../pages/ReferenceData.vue'
import UserManagement from '../pages/UserManagement.vue'
import HealthStatus from '../pages/HealthStatus.vue'
import Profile from '../pages/Profile.vue'
import { canAccessPage } from '../services/authService'

const routes = [
  { 
    path: '/login', 
    name: 'login',
    component: Login,
    meta: { requiresAuth: false }
  },
  { 
    path: '/test', 
    name: 'test',
    component: Test,
    meta: { requiresAuth: false }
  },
  { 
    path: '/database-test', 
    name: 'database-test',
    component: DatabaseTest,
    meta: { requiresAuth: false }
  },
  { 
    path: '/api-test', 
    name: 'api-test',
    component: ApiTest,
    meta: { requiresAuth: false }
  },
  { 
    path: '/websocket-test', 
    name: 'websocket-test',
    component: WebSocketTest,
    meta: { requiresAuth: false }
  },
  { 
    path: '/native-websocket-test', 
    name: 'native-websocket-test',
    component: NativeWebSocketTest,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      { 
        path: '', 
        redirect: '/dashboard'
      },
      { 
        path: '/dashboard', 
        name: 'dashboard',
        component: Dashboard,
        meta: { requiresAuth: true, roles: ['ADMIN', 'USER'] }
      },
      { 
        path: '/flights', 
        name: 'flights',
        component: FlightList,
        meta: { requiresAuth: true, roles: ['ADMIN', 'USER'] }
      },
      { 
        path: '/add-flight', 
        name: 'add-flight',
        component: FlightAdd,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/create-flight', 
        name: 'create-flight',
        component: FlightCreate,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/edit-flight/:id', 
        name: 'edit-flight',
        component: FlightEdit,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/flight-archive', 
        name: 'flight-archive',
        component: FlightArchive,
        meta: { requiresAuth: true, roles: ['ADMIN', 'USER'] }
      },
      { 
        path: '/bulk-upload', 
        name: 'bulk-upload',
        component: FlightBulkUpload,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/reference-data', 
        name: 'reference-data',
        component: ReferenceData,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/user-management', 
        name: 'user-management',
        component: UserManagement,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/health', 
        name: 'health',
        component: HealthStatus,
        meta: { requiresAuth: true, roles: ['ADMIN'] }
      },
      { 
        path: '/profile', 
        name: 'profile',
        component: Profile,
        meta: { requiresAuth: true, roles: ['ADMIN', 'USER'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authentication and authorization
router.beforeEach((to, from, next) => {
  const token = sessionStorage.getItem('jwt')
  
  // Test sayfaları için authentication gerekmez
  if (to.path === '/test' || to.path === '/database-test' || to.path === '/api-test' || to.path === '/websocket-test' || to.path === '/native-websocket-test') {
    next()
    return
  }
  
  // Login sayfasına gidiyorsa ve token varsa dashboard'a yönlendir
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }
  
  // Authentication gerektiren sayfalara gidiyorsa ve token yoksa login'e yönlendir
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // Rol tabanlı erişim kontrolü - sadece authentication gerektiren sayfalar için
  if (to.meta.requiresAuth && to.meta.roles && to.name && token) {
    const pageName = to.name
    
    if (!canAccessPage(pageName)) {
      // Yetkisiz erişim durumunda dashboard'a yönlendir
      next('/dashboard')
      return
    }
  }
  
  // Diğer durumlar için devam et
  next()
})

export default router
