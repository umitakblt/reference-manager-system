import apiClient from './api';

export const getUsers = async () => {
  try {
    const response = await apiClient.get('/api/admin/users');
    return response.data;
  } catch (error) {
    console.error("Failed to fetch users:", error);
    throw error;
  }
};

export const createUser = async (userData) => {
  try {
    const response = await apiClient.post('/api/admin/users', userData);
    return response.data;
  } catch (error) {
    console.error("Failed to create user:", error);
    throw error;
  }
}; 

export const deleteUser = async (userId) => {
  try {
    const response = await apiClient.delete(`/api/admin/users/${userId}`);
    return response.data;
  } catch (error) {
    console.error("Failed to delete user:", error);
    throw error;
  }
};

export const updateUser = async (userId, userData) => {
  try {
    const response = await apiClient.put(`/api/admin/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    console.error("Failed to update user:", error);
    throw error;
  }
}; 