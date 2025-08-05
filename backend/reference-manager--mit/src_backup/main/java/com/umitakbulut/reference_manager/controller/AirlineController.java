package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.AirlineRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AirlineResponseDTO;
import com.umitakbulut.reference_manager.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airlines")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    @GetMapping
    public ResponseEntity<List<AirlineResponseDTO>> getAllAirlines() {
        return ResponseEntity.ok(airlineService.getAllAirlines());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineResponseDTO> getAirlineById(@PathVariable Long id) {
        return ResponseEntity.ok(airlineService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AirlineResponseDTO> createAirline(@RequestBody @Valid AirlineRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(airlineService.save(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineResponseDTO> updateAirline(@PathVariable Long id, @RequestBody @Valid AirlineRequestDTO requestDTO) {
        return ResponseEntity.ok(airlineService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAirline(@PathVariable Long id) {
        airlineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
