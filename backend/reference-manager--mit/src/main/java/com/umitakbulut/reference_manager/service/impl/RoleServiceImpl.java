package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.RoleRequestDTO;
import com.umitakbulut.reference_manager.dto.response.RoleResponseDTO;
import com.umitakbulut.reference_manager.entity.Role;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.RoleMapper;
import com.umitakbulut.reference_manager.repository.RoleRepository;
import com.umitakbulut.reference_manager.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final KafkaProducer kafkaProducer;

    @Override
    public RoleResponseDTO save(RoleRequestDTO requestDTO) {
        Role role = roleMapper.toEntity(requestDTO);
        Role saved = roleRepository.save(role);

        kafkaProducer.sendRole(saved);

        return roleMapper.toDto(saved);
    }

    @Override
    public List<RoleResponseDTO> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
        return roleMapper.toDto(role);
    }

    @Override
    public RoleResponseDTO update(Long id, RoleRequestDTO requestDTO) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found with id: " + id));
        existing.setName(requestDTO.getName());
        existing.setDescription(requestDTO.getDescription());
        Role updated = roleRepository.save(existing);
        return roleMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }
}