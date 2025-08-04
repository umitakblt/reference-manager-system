package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.StationRequestDTO;
import com.umitakbulut.reference_manager.dto.response.StationResponseDTO;
import com.umitakbulut.reference_manager.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<StationResponseDTO> createStation(@RequestBody @Valid StationRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.save(requestDTO));
    }

    @GetMapping
    public ResponseEntity<List<StationResponseDTO>> getAllStations() {
        return ResponseEntity.ok(stationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StationResponseDTO> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StationResponseDTO> updateStation(@PathVariable Long id, @RequestBody @Valid StationRequestDTO requestDTO) {
        return ResponseEntity.ok(stationService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
