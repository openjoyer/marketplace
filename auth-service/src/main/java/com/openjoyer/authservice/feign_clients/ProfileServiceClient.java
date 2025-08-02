package com.openjoyer.authservice.feign_clients;

import com.openjoyer.authservice.model.Profile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "profile-service")
public interface ProfileServiceClient {

    @GetMapping("/api/profile/confirmation-token")
    ResponseEntity<Profile> getByConfirmationToken(@RequestParam("token") String token);

    @GetMapping("/api/profile")
    ResponseEntity<?> getProfile(@RequestHeader("X-User-Id") String id);

    @PutMapping("/api/profile")
    ResponseEntity<?> updateProfile(@RequestHeader("X-User-Id") String id, @RequestBody Profile profile);

    @GetMapping("/api/profile/email/verified")
    boolean isEmailVerified(@RequestHeader("X-User-Id") String id);
}