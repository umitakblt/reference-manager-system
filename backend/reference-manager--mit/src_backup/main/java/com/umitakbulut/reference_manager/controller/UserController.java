package com.umitakbulut.reference_manager.controller;

import com.umitakbulut.reference_manager.dto.request.RegisterRequestDTO;
import com.umitakbulut.reference_manager.dto.request.UserUpdateRequestDTO;
import com.umitakbulut.reference_manager.dto.response.UserResponseDTO;
import com.umitakbulut.reference_manager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debugUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        StringBuilder sb = new StringBuilder();
        sb.append("Kullanıcı Listesi:\n");
        for (UserResponseDTO user : users) {
            sb.append(String.format("ID: %d, Username: %s, Email: %s, Role: %s\n", 
                user.getId(), user.getUsername(), user.getEmail(), user.getRole()));
        }
        return ResponseEntity.ok(sb.toString());
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRole(id, role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid RegisterRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.createUser(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.updateUser(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}