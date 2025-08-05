package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.AircraftRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AircraftResponseDTO;
import com.umitakbulut.reference_manager.entity.Aircraft;
import com.umitakbulut.reference_manager.exception.NotFoundException;
import com.umitakbulut.reference_manager.kafka.KafkaProducer;
import com.umitakbulut.reference_manager.mapper.AircraftMapper;
import com.umitakbulut.reference_manager.repository.AircraftRepository;
import com.umitakbulut.reference_manager.service.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AircraftMapper aircraftMapper;
    private final KafkaProducer kafkaProducer;

    @Override
    public AircraftResponseDTO save(AircraftRequestDTO requestDTO) {
        Aircraft aircraft = aircraftMapper.toEntity(requestDTO);
        Aircraft saved = aircraftRepository.save(aircraft);

        kafkaProducer.sendAircraft(saved);

        return aircraftMapper.toDto(saved);
    }

    @Override
    public List<AircraftResponseDTO> getAll() {
        return aircraftRepository.findAll()
                .stream()
                .map(aircraftMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public AircraftResponseDTO getById(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        return aircraftMapper.toDto(aircraft);
    }

    @Override
    public AircraftResponseDTO update(Long id, AircraftRequestDTO requestDTO) {
        Aircraft existing = aircraftRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aircraft not found"));
        Aircraft updated = aircraftMapper.toEntity(requestDTO);
        updated.setId(existing.getId());

        Aircraft saved = aircraftRepository.save(updated);

        kafkaProducer.sendAircraft(saved);

        return aircraftMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        aircraftRepository.deleteById(id);
    }
}