package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.UserRoleRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserRoleResponseDTO;
import com.umitakbulut.reference_manager.entity.User;
import com.umitakbulut.reference_manager.entity.UserRole;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.UserRoleMapper;
import com.umitakbulut.reference_manager.repository.RoleRepository;
import com.umitakbulut.reference_manager.repository.UserRepository;
import com.umitakbulut.reference_manager.repository.UserRoleRepository;
import com.umitakbulut.reference_manager.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserRoleMapper userRoleMapper;
    private final KafkaProducer kafkaProducer;
    private final RoleRepository roleRepository;

    @Override
    public UserRoleResponseDTO assignRole(UserRoleRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(dto.getRole());

        UserRole saved = userRoleRepository.save(userRole);

        kafkaProducer.sendUserRole(saved);

        return userRoleMapper.toDto(saved);
    }

    @Override
    public List<UserRoleResponseDTO> getAllUserRoles() {
        return userRoleRepository.findAll()
                .stream()
                .map(userRoleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveFromKafka(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}