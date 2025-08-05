package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.FlightTypeRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightTypeResponseDTO;
import com.umitakbulut.reference_manager.entity.FlightType;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.FlightTypeMapper;
import com.umitakbulut.reference_manager.repository.FlightTypeRepository;
import com.umitakbulut.reference_manager.service.FlightTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightTypeServiceImpl implements FlightTypeService {

    private final FlightTypeRepository flightTypeRepository;
    private final FlightTypeMapper flightTypeMapper;
    private final KafkaProducer kafkaProducer;

    @Override
    public FlightTypeResponseDTO save(FlightTypeRequestDTO requestDTO) {
        FlightType entity = flightTypeMapper.toEntity(requestDTO);
        FlightType saved = flightTypeRepository.save(entity);

        kafkaProducer.sendMessage("flight-type-topic", saved);

        return flightTypeMapper.toDto(saved);
    }

    @Override
    public void saveFromKafka(FlightType flightType) {

        flightType.setId(null);
        flightTypeRepository.save(flightType);
    }

    @Override
    public List<FlightTypeResponseDTO> getAll() {
        return flightTypeRepository.findAll()
                .stream()
                .map(flightTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlightTypeResponseDTO getById(Long id) {
        FlightType flightType = flightTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FlightType not found"));
        return flightTypeMapper.toDto(flightType);
    }

    @Override
    public FlightTypeResponseDTO update(Long id, FlightTypeRequestDTO requestDTO) {
        FlightType existing = flightTypeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FlightType not found"));
        FlightType updated = flightTypeMapper.toEntity(requestDTO);
        updated.setId(existing.getId());
        FlightType saved = flightTypeRepository.save(updated);

        kafkaProducer.sendMessage("flight-type-topic", saved);

        return flightTypeMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        flightTypeRepository.deleteById(id);
    }
}