<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <h2>Kullanıcı Yönetimi</h2>
      </template>
      
      <el-table :data="users" v-loading="loading" style="width: 100%">
        <el-table-column prop="username" label="Kullanıcı Adı" width="150" />
        <el-table-column prop="email" label="E-posta" width="200" />
        <el-table-column prop="firstName" label="Ad" width="120" />
        <el-table-column prop="lastName" label="Soyad" width="120" />
        <el-table-column prop="roles" label="Roller" width="200">
          <template #default="scope">
            <el-tag 
              v-for="role in scope.row.roles" 
              :key="role.id" 
              size="small"
              style="margin-right: 5px;"
            >
              {{ role.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="Durum" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
              {{ scope.row.enabled ? 'Aktif' : 'Pasif' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="İşlemler" width="200">
          <template #default="scope">
            <el-button size="small" @click="editUser(scope.row.id)">
              Düzenle
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">
              Sil
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../services/api'

export default {
  name: 'UserManagement',
  setup() {
    const users = ref([])
    const loading = ref(false)
    
    const loadUsers = async () => {
      loading.value = true
      try {
        const response = await api.get('/users')
        users.value = response.data
      } catch (error) {
        ElMessage.error('Kullanıcılar yüklenirken hata oluştu')
        console.error('Error loading users:', error)
      } finally {
        loading.value = false
      }
    }
    
    const editUser = (id) => {
      // Kullanıcı düzenleme sayfasına yönlendir
      console.log('Edit user:', id)
    }
    
    const deleteUser = async (id) => {
      try {
        await ElMessageBox.confirm(
          'Bu kullanıcıyı silmek istediğinizden emin misiniz?',
          'Uyarı',
          {
            confirmButtonText: 'Evet',
            cancelButtonText: 'İptal',
            type: 'warning',
          }
        )
        
        await api.delete(`/users/${id}`)
        ElMessage.success('Kullanıcı başarıyla silindi')
        await loadUsers()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('Kullanıcı silinirken hata oluştu')
          console.error('Error deleting user:', error)
        }
      }
    }
    
    onMounted(() => {
      loadUsers()
    })
    
    return {
      users,
      loading,
      editUser,
      deleteUser
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}
</style> 