package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.AuthenticationRequestDTO;
import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AuthenticationResponseDTO;
import com.umitakbulut.reference_manager.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody RegisterRequestDTO request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }
} 