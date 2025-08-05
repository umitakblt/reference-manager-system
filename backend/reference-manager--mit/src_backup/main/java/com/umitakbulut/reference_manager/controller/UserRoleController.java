package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.UserRoleRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserRoleResponseDTO;
import com.umitakbulut.reference_manager.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/userroles")
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserRoleResponseDTO> assignRole(@RequestBody @Valid UserRoleRequestDTO requestDTO) {
        UserRoleResponseDTO response = userRoleService.assignRole(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
