package com.umitakbulut.reference_manager.service.impl;

import com.umitakbulut.reference_manager.dto.request.AuthenticationRequestDTO;
import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.response.AuthenticationResponseDTO;
import com.umitakbulut.reference_manager.entity.User;
import com.umitakbulut.reference_manager.repository.UserRepository;
import com.umitakbulut.reference_manager.security.JwtService;
import com.umitakbulut.reference_manager.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponseDTO register(RegisterRequestDTO request) {
        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        
        // Kullanıcı rollerini al
        List<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        
        return new AuthenticationResponseDTO(
                jwtToken,
                user.getUsername(),
                user.getEmail(),
                user.getId(),
                roles
        );
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        
        // Kullanıcı rollerini al
        List<String> roles = user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        
        return new AuthenticationResponseDTO(
                jwtToken,
                user.getUsername(),
                user.getEmail(),
                user.getId(),
                roles
        );
    }
} 