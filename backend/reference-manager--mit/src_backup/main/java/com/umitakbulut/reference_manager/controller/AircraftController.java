package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.AircraftRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AircraftResponseDTO;
import com.umitakbulut.reference_manager.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/aircrafts")
@RequiredArgsConstructor
public class AircraftController {

    private final AircraftService aircraftService;

    @GetMapping
    public ResponseEntity<List<AircraftResponseDTO>> getAllAircrafts() {
        return ResponseEntity.ok(aircraftService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponseDTO> getAircraftById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AircraftResponseDTO> createAircraft(@RequestBody @Valid AircraftRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(aircraftService.save(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponseDTO> updateAircraft(@PathVariable Long id, @RequestBody @Valid AircraftRequestDTO requestDTO) {
        return ResponseEntity.ok(aircraftService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
