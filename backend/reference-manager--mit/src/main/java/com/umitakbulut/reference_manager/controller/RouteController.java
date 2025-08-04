package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.RouteRequestDTO;
import com.umitakbulut.reference_manager.dto.response.RouteResponseDTO;
import com.umitakbulut.reference_manager.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<RouteResponseDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RouteResponseDTO> createRoute(@RequestBody @Valid RouteRequestDTO requestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.save(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> updateRoute(@PathVariable Long id, @RequestBody @Valid RouteRequestDTO requestDTO) {
        return ResponseEntity.ok(routeService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
