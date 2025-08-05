package com.umitakbulut.reference_manager.dto.request;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4687001846283212805L;

    private String email;
    private String firstName;
    private String lastName;
    private String role = "USER";
    private Boolean enabled = true;
    private String password;
} 