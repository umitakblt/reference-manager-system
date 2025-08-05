package com.umitakbulut.reference_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticationResponseDTO {
    private String token;
    private String username;
    private String email;
    private Long userId;
    private List<String> roles;
} 