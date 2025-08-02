package com.openjoyer.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile implements UserDetails {
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
