package com.openjoyer.authservice.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailConfirmationService {

    private final int expiryHours = 3;

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public LocalDateTime calculateExpiryHours() {
        return LocalDateTime.now().plusHours(expiryHours);
    }
}
