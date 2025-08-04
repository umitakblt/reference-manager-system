package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.FlightRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightResponseDTO;
import com.umitakbulut.reference_manager.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getFlightById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FlightResponseDTO> createFlight(@RequestBody @Valid FlightRequestDTO requestDTO) {
        FlightResponseDTO createdFlight = flightService.createFlight(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlight);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> updateFlight(@PathVariable Long id, @RequestBody @Valid FlightRequestDTO requestDTO) {
        FlightResponseDTO updatedFlight = flightService.updateFlight(id, requestDTO);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/archive")
    public ResponseEntity<List<FlightResponseDTO>> getArchivedFlights() {
        return ResponseEntity.ok(flightService.getArchivedFlights());
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<String> bulkUploadFlights(@RequestParam("file") MultipartFile file) {
        flightService.bulkUploadFlights(file);
        return ResponseEntity.ok("Bulk upload completed successfully");
    }
}
