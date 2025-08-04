<template>
  <div class="flight-bulk-upload">
    <el-card>
      <template #header>
        <h2>Toplu Uçuş Yükleme</h2>
      </template>
      
      <div class="upload-section">
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :auto-upload="false"
          :on-change="handleFileChange"
          :show-file-list="false"
          accept=".csv,.xlsx,.xls"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            Dosyayı buraya sürükleyin veya <em>tıklayarak seçin</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              CSV, XLSX veya XLS dosyaları kabul edilir
            </div>
          </template>
        </el-upload>
        
        <div v-if="selectedFile" class="file-info">
          <p><strong>Seçilen dosya:</strong> {{ selectedFile.name }}</p>
          <p><strong>Boyut:</strong> {{ formatFileSize(selectedFile.size) }}</p>
        </div>
        
        <div class="upload-actions">
          <el-button 
            type="primary" 
            @click="uploadFile" 
            :loading="uploading"
            :disabled="!selectedFile"
          >
            Yükle
          </el-button>
          <el-button @click="clearFile" :disabled="!selectedFile">
            Temizle
          </el-button>
        </div>
        
        <div v-if="uploadResult" class="upload-result">
          <h3>Yükleme Sonucu:</h3>
          <pre>{{ uploadResult }}</pre>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import api from '../services/api'

export default {
  name: 'FlightBulkUpload',
  components: {
    UploadFilled
  },
  setup() {
    const selectedFile = ref(null)
    const uploading = ref(false)
    const uploadResult = ref(null)
    
    const handleFileChange = (file) => {
      selectedFile.value = file.raw
      uploadResult.value = null
    }
    
    const clearFile = () => {
      selectedFile.value = null
      uploadResult.value = null
    }
    
    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
    }
    
    const uploadFile = async () => {
      if (!selectedFile.value) {
        ElMessage.warning('Lütfen bir dosya seçin')
        return
      }
      
      uploading.value = true
      uploadResult.value = null
      
      try {
        const formData = new FormData()
        formData.append('file', selectedFile.value)
        
        const response = await api.post('/flights/bulk-upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        uploadResult.value = response.data
        ElMessage.success('Dosya başarıyla yüklendi')
      } catch (error) {
        ElMessage.error('Dosya yüklenirken hata oluştu')
        console.error('Upload error:', error)
        uploadResult.value = error.response?.data || error.message
      } finally {
        uploading.value = false
      }
    }
    
    return {
      selectedFile,
      uploading,
      uploadResult,
      handleFileChange,
      clearFile,
      formatFileSize,
      uploadFile
    }
  }
}
</script>

<style scoped>
.flight-bulk-upload {
  padding: 20px;
}

.upload-section {
  text-align: center;
}

.file-info {
  margin: 20px 0;
  text-align: left;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.upload-actions {
  margin: 20px 0;
}

.upload-result {
  margin-top: 20px;
  text-align: left;
}

.upload-result pre {
  background-color: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
}
</style> 