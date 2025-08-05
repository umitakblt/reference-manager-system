package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.entity.FlightStatus;
import com.umitakbulut.reference_manager.service.FlightArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/flights/archive")
@RequiredArgsConstructor
public class FlightArchiveController {

    private final FlightArchiveService flightArchiveService;

    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAllArchivedFlights() {
        List<FlightResponseDTO> archivedFlights = flightArchiveService.getAllArchivedFlights()
                .stream()
                .map(archive -> {
                    FlightResponseDTO dto = new FlightResponseDTO();
                    dto.setId(archive.getId());
                    dto.setFlightNumber(archive.getFlightNumber());
                    dto.setAirlineId(archive.getAirlineId());
                    dto.setAircraftId(archive.getAircraftId());
                    dto.setOriginStationId(archive.getOriginStationId());
                    dto.setDestinationStationId(archive.getDestinationStationId());
                    dto.setAirlineName(archive.getAirlineName());
                    dto.setAircraftModel(archive.getAircraftModel());
                    dto.setOriginStationCode(archive.getOriginStationCode());
                    dto.setDestinationStationCode(archive.getDestinationStationCode());
                    dto.setOriginStationName(archive.getOriginStationCode());
                    dto.setDestinationStationName(archive.getDestinationStationCode());
                    dto.setScheduledDeparture(archive.getScheduledDeparture());
                    dto.setScheduledArrival(archive.getScheduledArrival());
                    dto.setFlightTypeName(archive.getFlightTypeName());
                    dto.setStatus(archive.getStatus() != null ? FlightStatus.valueOf(archive.getStatus()) : null);
                    dto.setDescription(archive.getDescription());
                    dto.setArchivedAt(archive.getArchivedAt());
                    return dto;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(archivedFlights);
    }

    @PostMapping("/update-archived-at")
    public ResponseEntity<String> updateArchivedAtForExistingRecords() {
        flightArchiveService.updateArchivedAtForExistingRecords();
        return ResponseEntity.ok("Arşivlenme tarihleri güncellendi");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArchivedFlight(@PathVariable Long id) {
        flightArchiveService.deleteArchivedFlight(id);
        return ResponseEntity.ok("Arşivlenmiş uçuş silindi");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllArchivedFlights() {
        flightArchiveService.deleteAllArchivedFlights();
        return ResponseEntity.ok("Tüm arşivlenmiş uçuşlar silindi");
    }
    
} 