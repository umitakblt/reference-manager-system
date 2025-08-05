package com.umitakbulut.reference_manager.service;

import com.umitakbulut.reference_manager.dto.request.AuthenticationRequestDTO;
import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AuthenticationResponseDTO;

public interface AuthenticationService {
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    AuthenticationResponseDTO register(RegisterRequestDTO request);
} 