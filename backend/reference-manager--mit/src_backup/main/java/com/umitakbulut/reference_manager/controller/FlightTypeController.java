package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.FlightTypeRequestDTO;
import com.umitakbulut.reference_manager.dto.response.FlightTypeResponseDTO;
import com.umitakbulut.reference_manager.service.FlightTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flight-types")
@RequiredArgsConstructor
public class FlightTypeController {

    private final FlightTypeService flightTypeService;

    @GetMapping
    public ResponseEntity<List<FlightTypeResponseDTO>> getAllFlightTypes() {
        return ResponseEntity.ok(flightTypeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightTypeResponseDTO> getFlightTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(flightTypeService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FlightTypeResponseDTO> createFlightType(@RequestBody @Valid FlightTypeRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightTypeService.save(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightTypeResponseDTO> updateFlightType(@PathVariable Long id, @RequestBody @Valid FlightTypeRequestDTO requestDTO) {
        return ResponseEntity.ok(flightTypeService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteFlightType(@PathVariable Long id) {
        flightTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
