package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.dto.request.FlightRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FlightService {


    FlightResponseDTO addFlight(FlightRequestDTO requestDTO);

    FlightResponseDTO createFlight(FlightRequestDTO flightRequestDTO);

    FlightResponseDTO getFlightById(Long id);

    List<FlightResponseDTO> getAllFlights();

    FlightResponseDTO updateFlight(Long id, FlightRequestDTO flightRequestDTO);

    void deleteFlight(Long id);

    List<FlightResponseDTO> getArchivedFlights();

    void bulkUploadFlights(MultipartFile file);
}
