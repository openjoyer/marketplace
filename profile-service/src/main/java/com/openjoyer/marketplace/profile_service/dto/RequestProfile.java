package com.openjoyer.marketplace.profile_service.dto;

import com.openjoyer.marketplace.profile_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
