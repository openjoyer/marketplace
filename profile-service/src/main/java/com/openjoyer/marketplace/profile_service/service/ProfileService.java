package com.openjoyer.marketplace.profile_service.service;

import com.openjoyer.marketplace.profile_service.dto.ResponseProfile;
import com.openjoyer.marketplace.profile_service.exceptions.AddressExistsException;
import com.openjoyer.marketplace.profile_service.exceptions.EmailExistsException;
import com.openjoyer.marketplace.profile_service.model.Address;
import com.openjoyer.marketplace.profile_service.model.Profile;
import com.openjoyer.marketplace.profile_service.exceptions.ProfileNotFoundException;
import com.openjoyer.marketplace.profile_service.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileService {

    private final ProfileRepository profileRepository;

    public void save(Profile profile) {
        if (profileRepository.findByEmail(profile.getEmail()).isPresent()) {
            throw new EmailExistsException();
        }
        profile.setCreatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        log.info("profile created: {}", profile.getId());
    }

    public ResponseProfile getProfile(String id) throws ProfileNotFoundException {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
        return mapToResponseProfile(profile);
    }

    public Profile getProfileByEmail(String email) throws ProfileNotFoundException {
        return profileRepository.findByEmail(email).orElse(null);
    }

    public String getEmail(String id) throws ProfileNotFoundException {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
        return profile.getEmail();
    }

    public void updateAddress(String userId, Address address) throws ProfileNotFoundException {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new ProfileNotFoundException(userId));
        profile.setAddress(address);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
    }

    public void addUserAddress(String userId, Address address) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new ProfileNotFoundException(userId));
        if (profile.getAddress() != null) {
            throw new AddressExistsException(userId);
        }
        profile.setAddress(address);
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
    }
    public Address getAddress(String id) throws ProfileNotFoundException {
        Profile profile = profileRepository.findById(id).orElseThrow(() -> new ProfileNotFoundException(id));
        return profile.getAddress();
    }

    public Profile getProfileByConfirmationToken(String token) throws ProfileNotFoundException {
        return profileRepository.findByConfirmationToken(token).orElse(null);
    }

    public List<ResponseProfile> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();
        return profiles.stream().map(this::mapToResponseProfile).collect(Collectors.toList());
    }

    public ResponseProfile updateProfile(String id, Profile profile) {
        if (profileRepository.findById(id).orElse(null) == null) {
            throw new ProfileNotFoundException(id);
        }
        profile.setUpdatedAt(LocalDateTime.now());
        profileRepository.save(profile);
        log.info("profile updated: {}", id);

        return mapToResponseProfile(profile);
    }

    public void deleteProfile(String id) {
        profileRepository.deleteById(id);
        log.info("profile deleted: {}", id);
    }

    private ResponseProfile mapToResponseProfile(Profile profile) {
        ResponseProfile responseProfile = ResponseProfile.builder()
                .id(profile.getId())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .balance(profile.getBalance())
                .isEmailVerified(profile.isEmailVerified())
                .address(profile.getAddress())
                .role(profile.getRole())
                .confirmationToken(profile.getConfirmationToken())
                .confirmationTokenExpiry(profile.getConfirmationTokenExpiry())
                .password(profile.getPassword())
                .birthDate(profile.getBirthDate())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
        return responseProfile;
    }
}

