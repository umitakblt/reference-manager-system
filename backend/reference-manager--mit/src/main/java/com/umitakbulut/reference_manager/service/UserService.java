package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserResponseDTO;
import com.umitakbulut.reference_manager.entity.User;
import jakarta.validation.Valid;

import java.util.List;

public interface UserService {
    UserResponseDTO getUserById(Long id);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUser(Long id, RegisterRequestDTO requestDTO);

    UserResponseDTO createUser(RegisterRequestDTO requestDTO);

    UserResponseDTO updateUserRole(Long id, String role);

    void deleteUser(Long id);

    void saveFromKafka(User user);

}
