package com.openjoyer.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openjoyer.authservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    // yyyy-MM-dd
    @JsonProperty("birth_date")
    private LocalDate birthDate;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("confirm_password")
    private String confirmPassword;
    @JsonProperty("role")
    private Role role;
}
