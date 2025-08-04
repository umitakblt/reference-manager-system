package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.dto.request.FlightTypeRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightTypeResponseDTO;
import com.umitakbulut.reference_manager.entity.FlightType;

import java.util.List;

public interface FlightTypeService {

    FlightTypeResponseDTO save(FlightTypeRequestDTO requestDTO);
    FlightTypeResponseDTO getById(Long id);
    List<FlightTypeResponseDTO> getAll();
    FlightTypeResponseDTO update(Long id, FlightTypeRequestDTO requestDTO);
    void saveFromKafka(FlightType flightType);
    void delete(Long id);
}
