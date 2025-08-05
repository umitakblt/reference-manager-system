package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.dto.request.UserRoleRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserRoleResponseDTO;
import com.umitakbulut.reference_manager.entity.UserRole;

import java.util.List;

public interface UserRoleService {
    UserRoleResponseDTO assignRole(UserRoleRequestDTO dto);

    List<UserRoleResponseDTO> getAllUserRoles();

    void saveFromKafka(UserRole userRole);
}