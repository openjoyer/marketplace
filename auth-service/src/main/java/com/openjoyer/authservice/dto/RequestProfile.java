package com.openjoyer.authservice.dto;

import com.openjoyer.authservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestProfile {
    private String firstName;
    private String lastName;
    // yyyy-MM-dd
    private String birthDate;
    private String email;
    private String password;

    private Role role;
}
