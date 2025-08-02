package com.openjoyer.marketplace.profile_service.dto;

import com.openjoyer.marketplace.profile_service.model.Address;
import com.openjoyer.marketplace.profile_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseProfile {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private boolean isEmailVerified;
    private String confirmationToken;
    private LocalDateTime confirmationTokenExpiry;
    private Address address;
    private String password;
    private Integer balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Role role;
}
