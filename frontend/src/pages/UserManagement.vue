<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <h2>Kullanıcı Yönetimi</h2>
      </template>
      
      <el-table :data="users" v-loading="loading" class="user-table" :header-cell-style="{ textAlign: 'center', backgroundColor: '#f5f7fa', color: '#606266', fontWeight: '600' }">
        <el-table-column prop="username" label="Kullanıcı Adı" min-width="150" align="center" />
        <el-table-column prop="email" label="E-posta" min-width="200" align="center" />
        <el-table-column prop="firstName" label="Ad" min-width="120" align="center">
          <template #default="scope">
            {{ scope.row.firstName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="lastName" label="Soyad" min-width="120" align="center">
          <template #default="scope">
            {{ scope.row.lastName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="roles" label="Roller" min-width="200" align="center">
          <template #default="scope">
            <el-tag 
              v-for="role in scope.row.roles || []" 
              :key="role.id" 
              size="small"
              style="margin-right: 5px;"
            >
              {{ role.name }}
            </el-tag>
            <span v-if="!scope.row.roles || scope.row.roles.length === 0" style="color: #999;">
              {{ scope.row.role || '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="Durum" min-width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.enabled ? 'success' : 'danger'">
              {{ scope.row.enabled ? 'Aktif' : 'Pasif' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="İşlemler" min-width="200" align="center">
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

    <!-- Kullanıcı Düzenleme Modal -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="Kullanıcı Düzenle" 
      width="500px"
      :before-close="handleCloseDialog"
    >
      <el-form 
        ref="editFormRef" 
        :model="editForm" 
        :rules="editRules" 
        label-width="120px"
      >
        <el-form-item label="Kullanıcı Adı" prop="username">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        
        <el-form-item label="E-posta" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        
        <el-form-item label="Ad" prop="firstName">
          <el-input v-model="editForm.firstName" />
        </el-form-item>
        
        <el-form-item label="Soyad" prop="lastName">
          <el-input v-model="editForm.lastName" />
        </el-form-item>
        
        <el-form-item label="Rol" prop="role">
          <el-select v-model="editForm.role" placeholder="Rol seçin" style="width: 100%">
            <el-option label="Kullanıcı" value="USER" />
            <el-option label="Yönetici" value="ADMIN" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="Durum" prop="enabled">
          <el-switch 
            v-model="editForm.enabled" 
            active-text="Aktif" 
            inactive-text="Pasif"
          />
        </el-form-item>
        
        <el-form-item label="Yeni Şifre" prop="password">
          <el-input 
            v-model="editForm.password" 
            type="password" 
            placeholder="Değiştirmek istemiyorsanız boş bırakın"
            show-password
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDialog">İptal</el-button>
          <el-button type="primary" @click="saveUser" :loading="saving">
            Kaydet
          </el-button>
        </span>
      </template>
    </el-dialog>
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
    const editDialogVisible = ref(false)
    const saving = ref(false)
    const editFormRef = ref(null)
    const editForm = ref({
      id: null,
      username: '',
      email: '',
      firstName: '',
      lastName: '',
      role: 'USER',
      enabled: true,
      password: ''
    })
    
    const editRules = {
      email: [
        { required: true, message: 'E-posta gerekli', trigger: 'blur' },
        { type: 'email', message: 'Geçerli bir e-posta girin', trigger: 'blur' }
      ],
      firstName: [
        { required: true, message: 'Ad gerekli', trigger: 'blur' }
      ],
      lastName: [
        { required: true, message: 'Soyad gerekli', trigger: 'blur' }
      ],
      role: [
        { required: true, message: 'Rol gerekli', trigger: 'change' }
      ]
    }
    
    const loadUsers = async () => {
      loading.value = true
      try {
        const response = await api.get('/v1/users')
        users.value = response.data
      } catch (error) {
        ElMessage.error('Kullanıcılar yüklenirken hata oluştu')
        console.error('Error loading users:', error)
      } finally {
        loading.value = false
      }
    }
    
    const editUser = (id) => {
      const user = users.value.find(u => u.id === id)
      if (user) {
        editForm.value = {
          id: user.id,
          username: user.username,
          email: user.email,
          firstName: user.firstName || '',
          lastName: user.lastName || '',
          role: user.role,
          enabled: user.enabled,
          password: ''
        }
        editDialogVisible.value = true
      }
    }
    
    const handleCloseDialog = () => {
      editDialogVisible.value = false
      editForm.value = {
        id: null,
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        role: 'USER',
        enabled: true,
        password: ''
      }
    }
    
    const saveUser = async () => {
      if (!editFormRef.value) return
      
      try {
        await editFormRef.value.validate()
        saving.value = true
        
        const updateData = {
          email: editForm.value.email,
          firstName: editForm.value.firstName,
          lastName: editForm.value.lastName,
          role: editForm.value.role,
          enabled: editForm.value.enabled
        }
        
        if (editForm.value.password && editForm.value.password.trim()) {
          updateData.password = editForm.value.password
        }
        
        console.log('🔄 Kullanıcı güncelleniyor:', updateData)
        const response = await api.put(`/v1/users/${editForm.value.id}`, updateData)
        console.log('✅ Kullanıcı güncelleme response:', response.data)
        
        // State'i hemen güncelle
        const userIndex = users.value.findIndex(u => u.id === editForm.value.id)
        if (userIndex !== -1) {
          users.value[userIndex] = { ...users.value[userIndex], ...response.data }
          console.log('✅ State güncellendi:', users.value[userIndex])
        }
        
        ElMessage.success('Kullanıcı başarıyla güncellendi')
        handleCloseDialog()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('Kullanıcı güncellenirken hata oluştu')
          console.error('Error updating user:', error)
        }
      } finally {
        saving.value = false
      }
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
        
        await api.delete(`/v1/users/${id}`)
        
        // State'den hemen sil
        const userIndex = users.value.findIndex(u => u.id === id)
        if (userIndex !== -1) {
          users.value.splice(userIndex, 1)
        }
        
        ElMessage.success('Kullanıcı başarıyla silindi')
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
      deleteUser,
      editDialogVisible,
      editForm,
      editFormRef,
      editRules,
      saving,
      handleCloseDialog,
      saveUser
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.user-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.user-table .el-table__header {
  text-align: center;
}

.user-table .el-table__header th {
  text-align: center;
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  padding: 12px 0;
  border-bottom: 2px solid #e4e7ed;
}

.user-table .el-table__body td {
  text-align: center;
  vertical-align: middle;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.user-table .el-table__body .cell {
  text-align: center;
}

.user-table .el-table__row:hover {
  background-color: #f5f7fa;
}

.user-table .el-table__row:nth-child(even) {
  background-color: #fafafa;
}

.user-table .el-table__row:nth-child(even):hover {
  background-color: #f0f2f5;
}

.user-table .el-button {
  margin: 0 2px;
}

.user-table .el-tag {
  margin: 2px;
}
</style> 