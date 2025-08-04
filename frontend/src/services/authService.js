import apiClient from './api';

export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/auth/authenticate', credentials);
    
    // On successful login, store token and user info
    if (response.data && response.data.token) {
      sessionStorage.setItem('jwt', response.data.token);
      sessionStorage.setItem('username', response.data.username);
      sessionStorage.setItem('email', response.data.email);
      
      // Kullanıcı rollerini de kaydet
      if (response.data.roles) {
        console.log('Backend\'den gelen roller:', response.data.roles);
        
        // Test için ümit kullanıcısının rollerini manuel olarak değiştir
        let roles = response.data.roles;
        if (response.data.username === 'umit') {
          roles = ['USER'];
          console.log('Ümit kullanıcısı için roller USER olarak değiştirildi');
        }
        
        sessionStorage.setItem('userRoles', JSON.stringify(roles));
      }
      
      // Kullanıcı ID'sini kaydet
      if (response.data.userId) {
        sessionStorage.setItem('userId', response.data.userId);
      }
      
      console.log('Login başarılı - Kullanıcı:', response.data.username, 'Roller:', response.data.roles);
    }
    
    return response;
  } catch (error) {
    console.error("Login failed:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export const register = async (userData) => {
  try {
    const response = await apiClient.post('/auth/register', userData);
    
    if (response.data && response.data.token) {
      sessionStorage.setItem('jwt', response.data.token);
      sessionStorage.setItem('username', response.data.username);
      sessionStorage.setItem('email', response.data.email);
      
      if (response.data.roles) {
        sessionStorage.setItem('userRoles', JSON.stringify(response.data.roles));
      }
      
      if (response.data.userId) {
        sessionStorage.setItem('userId', response.data.userId);
      }
    }
    
    return response.data;
  } catch (error) {
    console.error("Registration failed:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export const logout = () => {
  try {
    // Tüm session storage'ı temizle
    sessionStorage.clear()
    
    // Local storage'dan da JWT'yi temizle (eğer varsa)
    localStorage.removeItem('jwt')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
    localStorage.removeItem('userRoles')
    localStorage.removeItem('userId')
    
    // Axios interceptor'ları temizle
    if (apiClient && apiClient.defaults && apiClient.defaults.headers) {
      delete apiClient.defaults.headers.common['Authorization']
    }
    
    console.log('Logout başarılı - tüm veriler temizlendi')
  } catch (error) {
    console.error('Logout sırasında hata:', error)
  }
};

export function getToken() {
  return sessionStorage.getItem('jwt');
}

export function getUsername() {
  return sessionStorage.getItem('username');
}

export function getEmail() {
  return sessionStorage.getItem('email');
}

export function getUserId() {
  return sessionStorage.getItem('userId');
}

export function getUserRoles() {
  const roles = sessionStorage.getItem('userRoles');
  const parsedRoles = roles ? JSON.parse(roles) : [];
  return parsedRoles;
}

export function isAuthenticated() {
  return !!getToken();
}

// Rol tabanlı yetki kontrolü fonksiyonları
export function hasRole(role) {
  const userRoles = getUserRoles();
  return userRoles.includes(role);
}

export function hasAnyRole(roles) {
  const userRoles = getUserRoles();
  return roles.some(role => userRoles.includes(role));
}

export function hasAllRoles(roles) {
  const userRoles = getUserRoles();
  return roles.every(role => userRoles.includes(role));
}

export function isAdmin() {
  return hasRole('ADMIN');
}

export function isUser() {
  return hasRole('USER');
}

// Sayfa erişim yetkileri
export const PAGE_PERMISSIONS = {
  'dashboard': ['ADMIN', 'USER'],
  'flights': ['ADMIN', 'USER'],
  'add-flight': ['ADMIN'],
  'create-flight': ['ADMIN'],
  'edit-flight': ['ADMIN'],
  'flight-archive': ['ADMIN'], // USER kaldırıldı
  'bulk-upload': ['ADMIN'],
  'reference-data': ['ADMIN'],
  'user-management': ['ADMIN'],
  'health': ['ADMIN'],
  'profile': ['ADMIN', 'USER']
};

export function canAccessPage(pageName) {
  // Kullanıcı giriş yapmamışsa erişim yok
  if (!isAuthenticated()) {
    return false;
  }
  
  const userRoles = getUserRoles();
  const requiredRoles = PAGE_PERMISSIONS[pageName] || [];
  
  if (requiredRoles.length === 0) {
    return true; // Yetki belirtilmemişse erişime izin ver
  }
  
  // Rol eşleştirmesi - backend'den gelen roller ile frontend'deki roller arasında
  const hasAccess = requiredRoles.some(requiredRole => {
    // Backend'den gelen roller ile frontend'deki roller arasında eşleştirme
    return userRoles.some(userRole => {
      // ADMIN rolü için eşleştirme
      if (requiredRole === 'ADMIN' && (userRole === 'ADMIN' || userRole === 'ROLE_ADMIN')) {
        return true;
      }
      // USER rolü için eşleştirme
      if (requiredRole === 'USER' && (userRole === 'USER' || userRole === 'ROLE_USER')) {
        return true;
      }
      // Direkt eşleştirme
      return userRole === requiredRole;
    });
  });
  
  return hasAccess;
}

// İşlem yetkileri
export const ACTION_PERMISSIONS = {
  'create-flight': ['ADMIN'],
  'edit-flight': ['ADMIN'],
  'delete-flight': ['ADMIN'],
  'bulk-upload': ['ADMIN'],
  'manage-users': ['ADMIN'],
  'view-reports': ['ADMIN'], // USER kaldırıldı
  'export-data': ['ADMIN'],
  'view-archive': ['ADMIN'], // Yeni eklendi
  'view-system-status': ['ADMIN'] // Yeni eklendi
};

export function canPerformAction(action) {
  // Kullanıcı giriş yapmamışsa erişim yok
  if (!isAuthenticated()) {
    return false;
  }
  
  const userRoles = getUserRoles();
  const requiredRoles = ACTION_PERMISSIONS[action] || [];
  
  if (requiredRoles.length === 0) return true; // Yetki belirtilmemişse erişime izin ver
  
  // Rol eşleştirmesi - backend'den gelen roller ile frontend'deki roller arasında
  return requiredRoles.some(requiredRole => {
    // Backend'den gelen roller ile frontend'deki roller arasında eşleştirme
    return userRoles.some(userRole => {
      // ADMIN rolü için eşleştirme
      if (requiredRole === 'ADMIN' && (userRole === 'ADMIN' || userRole === 'ROLE_ADMIN')) {
        return true;
      }
      // USER rolü için eşleştirme
      if (requiredRole === 'USER' && (userRole === 'USER' || userRole === 'ROLE_USER')) {
        return true;
      }
      // Direkt eşleştirme
      return userRole === requiredRole;
    });
  });
} 