import apiClient from './api';

export const login = async (credentials) => {
  try {
    const response = await apiClient.post('/v1/auth/authenticate', credentials);
    
    if (response.data && response.data.token) {
      sessionStorage.setItem('jwt', response.data.token);
      sessionStorage.setItem('username', response.data.username);
      sessionStorage.setItem('email', response.data.email);
      
      if (response.data.roles) {
        console.log('Backend\'den gelen roller:', response.data.roles);
        
        let roles = response.data.roles;
        if (response.data.username === 'umit') {
          roles = ['USER'];
          console.log('Ümit kullanıcısı için roller USER olarak değiştirildi');
        }
        
        sessionStorage.setItem('userRoles', JSON.stringify(roles));
      }
      
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
    const response = await apiClient.post('/v1/auth/register', userData);
    
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
    sessionStorage.clear()
    
    localStorage.removeItem('jwt')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
    localStorage.removeItem('userRoles')
    localStorage.removeItem('userId')
    
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

export const PAGE_PERMISSIONS = {
  'dashboard': ['ADMIN', 'USER'],
  'flights': ['ADMIN', 'USER'],
  'add-flight': ['ADMIN'],
  'create-flight': ['ADMIN'],
  'edit-flight': ['ADMIN'],
  'flight-archive': ['ADMIN'],
  'bulk-upload': ['ADMIN'],
  'reference-data': ['ADMIN'],
  'user-management': ['ADMIN'],
  'health': ['ADMIN'],
  'profile': ['ADMIN', 'USER']
};

export function canAccessPage(pageName) {
  if (!isAuthenticated()) {
    return false;
  }
  
  const userRoles = getUserRoles();
  const requiredRoles = PAGE_PERMISSIONS[pageName] || [];
  
  if (requiredRoles.length === 0) {
    return true;
  }
  
  const hasAccess = requiredRoles.some(requiredRole => {
    return userRoles.some(userRole => {
      if (requiredRole === 'ADMIN' && (userRole === 'ADMIN' || userRole === 'ROLE_ADMIN')) {
        return true;
      }
      if (requiredRole === 'USER' && (userRole === 'USER' || userRole === 'ROLE_USER')) {
        return true;
      }
      return userRole === requiredRole;
    });
  });
  
  return hasAccess;
}

export const ACTION_PERMISSIONS = {
  'create-flight': ['ADMIN'],
  'edit-flight': ['ADMIN'],
  'delete-flight': ['ADMIN'],
  'bulk-upload': ['ADMIN'],
  'manage-users': ['ADMIN'],
  'view-reports': ['ADMIN'],
  'export-data': ['ADMIN'],
  'view-archive': ['ADMIN'],
  'view-system-status': ['ADMIN']
};

export function canPerformAction(action) {
  if (!isAuthenticated()) {
    return false;
  }
  
  const userRoles = getUserRoles();
  const requiredRoles = ACTION_PERMISSIONS[action] || [];
  
  if (requiredRoles.length === 0) return true;
  
  return requiredRoles.some(requiredRole => {
    return userRoles.some(userRole => {
      if (requiredRole === 'ADMIN' && (userRole === 'ADMIN' || userRole === 'ROLE_ADMIN')) {
        return true;
      }
      if (requiredRole === 'USER' && (userRole === 'USER' || userRole === 'ROLE_USER')) {
        return true;
      }
      return userRole === requiredRole;
    });
  });
} 