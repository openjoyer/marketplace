package com.openjoyer.marketplace.profile_service.controller;

import com.openjoyer.marketplace.profile_service.dto.ResponseProfile;
import com.openjoyer.marketplace.profile_service.exceptions.AddressExistsException;
import com.openjoyer.marketplace.profile_service.exceptions.EmailExistsException;
import com.openjoyer.marketplace.profile_service.exceptions.ProfileNotFoundException;
import com.openjoyer.marketplace.profile_service.exceptions.ResponseHandler;
import com.openjoyer.marketplace.profile_service.model.Address;
import com.openjoyer.marketplace.profile_service.model.Profile;
import com.openjoyer.marketplace.profile_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllProfiles() {
        List<ResponseProfile> responseProfiles = profileService.getAllProfiles();
        if (responseProfiles.isEmpty()) {
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "no profiles found",
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseProfiles, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createProfile(@RequestBody Profile profile) {
        try {
            profileService.save(profile);
        } catch (EmailExistsException e) {
            ResponseHandler responseHandler = new ResponseHandler(400,
                    "email already exists: " + profile.getEmail(),
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<?> getProfileByEmail(@RequestHeader("email") String email) {
        Profile profile = profileService.getProfileByEmail(email);
        if (profile == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @GetMapping("/confirmation-token")
    public Profile getByConfirmationToken(@RequestParam("token") String token) {
        Profile profile = profileService.getProfileByConfirmationToken(token);
        return profile;
    }

    @GetMapping("/email/verified")
    boolean isEmailVerified(@RequestHeader("X-User-Id") String id) {
        ResponseProfile profile = profileService.getProfile(id);
        if (profile == null) {
            return false;
        }
        return profile.isEmailVerified();
    }

    @PostMapping("/address")
    public ResponseEntity<?> addAddress(@RequestHeader("X-User-Id") String id,
                                        @RequestBody Address address) {
        try {
            profileService.addUserAddress(id, address);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProfileNotFoundException e) {
            log.error("Profile not found: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AddressExistsException e) {
            log.error("Address already exists: {}", id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/address")
    public ResponseEntity<?> updateAddress(@RequestHeader("X-User-Id") String id,
                                        @RequestBody Address address) {
        try {
            profileService.addUserAddress(id, address);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProfileNotFoundException e) {
            log.error("Profile not found: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/address")
    public Address getAddress(@RequestHeader("X-User-Id") String id) {
        try {
            Address address = profileService.getAddress(id);
            return address;
        } catch (ProfileNotFoundException e) {
            log.error("Profile not found: {}", id);
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader("X-User-Id") String id) {
        try {
            ResponseProfile profile = profileService.getProfile(id);
            return new ResponseEntity<>(profile, HttpStatus.OK);
        } catch (ProfileNotFoundException e) {
            ResponseHandler handler = new ResponseHandler(404,
                    "profile not found: " + id,
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/get")
    public String getProfileEmail(@RequestHeader("X-User-Id") String id) {
        try {
            String email = profileService.getEmail(id);
            return email;
        } catch (ProfileNotFoundException e) {
            log.error("profile not found: {}", id);
            return null;
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestHeader("X-User-Id") String id, @RequestBody Profile profile) {
        try {
            ResponseProfile response = profileService.updateProfile(id, profile);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProfileNotFoundException e) {
            ResponseHandler handler = new ResponseHandler(404,
                    "profile with id "+ id + " not found",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping
    public void deleteProfile(@RequestHeader("X-User-Id") String id) {
        profileService.deleteProfile(id);
    }
}
