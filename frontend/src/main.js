import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './views/App.vue'
import router from './router'

const app = createApp(App)

// Pinia store'u ekle
app.use(createPinia())

// Element Plus UI kütüphanesini ekle
app.use(ElementPlus)

// Element Plus ikonlarını ekle
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Router'ı ekle
app.use(router)

app.mount('#app') 