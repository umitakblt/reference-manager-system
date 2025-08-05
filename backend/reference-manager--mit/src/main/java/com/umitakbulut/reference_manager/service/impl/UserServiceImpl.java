package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.request.UserUpdateRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserResponseDTO;
import com.umitakbulut.reference_manager.dto.response.RoleResponseDTO;
import com.umitakbulut.reference_manager.entity.User;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.UserMapper;
import com.umitakbulut.reference_manager.repository.UserRepository;
import com.umitakbulut.reference_manager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaProducer kafkaProducer;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUserWithRoles)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapUserWithRoles(User user) {
        UserResponseDTO dto = userMapper.toDto(user);
        RoleResponseDTO roleDto = new RoleResponseDTO();
        roleDto.setId(1L);
        roleDto.setName(user.getRole());
        roleDto.setDescription(user.getRole().equals("ADMIN") ? "Sistem Yöneticisi" : "Kullanıcı");
        dto.setRoles(List.of(roleDto));
        return dto;
    }

    @Override
    public UserResponseDTO createUser(RegisterRequestDTO requestDTO) {
        User user = userMapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        User saved = userRepository.save(user);
        
        kafkaProducer.sendUser(saved);
        
        return userMapper.toDto(saved);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO requestDTO) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        existing.setEmail(requestDTO.getEmail());
        existing.setFirstName(requestDTO.getFirstName());
        existing.setLastName(requestDTO.getLastName());
        existing.setRole(requestDTO.getRole());
        existing.setEnabled(requestDTO.getEnabled());
        
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().trim().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        
        User saved = userRepository.save(existing);

        kafkaProducer.sendUser(saved);

        return mapUserWithRoles(saved);
    }

    @Override
    public UserResponseDTO updateUserRole(Long id, String role) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        existing.setRole(role);
        User saved = userRepository.save(existing);

        kafkaProducer.sendUser(saved);

        return userMapper.toDto(saved);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void saveFromKafka(User user) {
        userRepository.save(user);
    }
}