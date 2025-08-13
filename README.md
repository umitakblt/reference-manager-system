# Reference Manager Projesi

## Kurulum

1. Java 17+ kurulu olmalı.
2. Backend için:
   ```sh
   cd backend/reference-manager--mit
   ./mvnw spring-boot:run
   ```
3. Frontend için:
   ```sh
   cd frontend
   npm install
   npm run dev
   ```

## API Dokümantasyonu

Backend çalışırken [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) adresinden otomatik API dokümantasyonuna ulaşabilirsiniz.

## Geliştirici Notları
- Kodda ortak yardımcı fonksiyonlar utils/lookup.js altında toplanmıştır.
- Hata yönetimi frontend ve backend'de merkezi olarak yapılmaktadır.
- Kodda sadeleştirme ve best practice uygulanmıştır. 
