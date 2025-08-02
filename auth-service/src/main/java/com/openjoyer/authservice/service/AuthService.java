package com.openjoyer.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.authservice.dto.AuthResponse;
import com.openjoyer.authservice.dto.LoginRequest;
import com.openjoyer.authservice.dto.RegisterRequest;
import com.openjoyer.authservice.dto.ResponseProfile;
import com.openjoyer.authservice.events.EmailConfirmationEvent;
import com.openjoyer.authservice.exceptions.ProfileServiceException;
import com.openjoyer.authservice.exceptions.ResponseHandler;
import com.openjoyer.authservice.feign_clients.ProfileServiceClient;
import com.openjoyer.authservice.model.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final WebClient webClient;
    private final EmailConfirmationService emailConfirmationService;
    private final KafkaService kafkaService;
    private final ProfileServiceClient profileServiceClient;

    public Profile register(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return null;
        }

        Profile profile = Profile.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .birthDate(registerRequest.getBirthDate())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .balance(0)
                .createdAt(LocalDateTime.now())
                .isEmailVerified(false)
                .confirmationToken(null)
                .confirmationTokenExpiry(null)
                .build();
        try {
            String id = sendSaveUserRequest(profile);
            profile.setId(id);
            return profile;
        } catch (ProfileServiceException e) {
            throw e;
        }
    }


    public void sendConfirmationEmail(ResponseProfile responseProfile) throws JsonProcessingException, ProfileServiceException {
        try {
            Profile profile = mapToProfile(responseProfile);
            String confirmationToken = emailConfirmationService.generateToken();
            profile.setConfirmationToken(confirmationToken);
            LocalDateTime expiryDate = emailConfirmationService.calculateExpiryHours();
            profile.setConfirmationTokenExpiry(expiryDate);
            profile.setUpdatedAt(LocalDateTime.now());
//            sendSaveUserRequest(profile);
            profileServiceClient.updateProfile(profile.getId(), profile);

            EmailConfirmationEvent emailConfirmationEvent = new EmailConfirmationEvent(
                    profile.getEmail(),
                    confirmationToken
            );
            kafkaService.sendConfirmationEmail(emailConfirmationEvent);
        } catch (ProfileServiceException | JsonProcessingException e){
            log.error("sendConfirmationEmail failed: ", e);
            throw e;
        }
    }

    public Profile authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var user = sendGetUserRequest(loginRequest.getEmail()).block();
        if ((user != null) && (user.getEmail().equals(loginRequest.getEmail()))) {
            return user;
        }
        return null;
    }

    public String sendSaveUserRequest(Profile profile) {
        try {
            return webClient.post()
                    .uri("lb://profile-service/api/profile")
                    .bodyValue(profile)
                    .retrieve()
                    .onStatus(
                            status -> status == HttpStatus.BAD_REQUEST,
                            response -> response.bodyToMono(ResponseHandler.class)
                                    .flatMap(errorBody -> Mono.error(new ProfileServiceException(errorBody.getMessage())))
                    )
                    .onStatus(
                            status -> status == HttpStatus.SERVICE_UNAVAILABLE,
                            response -> {
                                log.error("Profile service 503 error");
                                return Mono.error(new ProfileServiceException("Service unavailable"));
                            }
                    )
                    .bodyToMono(Profile.class)
                    .blockOptional()
                    .orElseThrow(() -> new ProfileServiceException("Empty response"))
                    .getId();
        } catch (ProfileServiceException e) {
            log.error("Failed to save profile: {}", e.getMessage());
            throw e;
        }
    }

    public Mono<Profile> sendGetUserRequest(String email) {
        return webClient.get()
                .uri("lb://profile-service/api/profile/email")
                .header("email", email)
                .retrieve()
                .bodyToMono(Profile.class);
    }

    private Profile mapToProfile(ResponseProfile responseProfile) {
        return Profile.builder()
                .id(responseProfile.getId())
                .email(responseProfile.getEmail())
                .birthDate(responseProfile.getBirthDate())
                .balance(responseProfile.getBalance())
                .role(responseProfile.getRole())
                .confirmationTokenExpiry(responseProfile.getConfirmationTokenExpiry())
                .confirmationToken(responseProfile.getConfirmationToken())
                .password(responseProfile.getPassword())
                .address(responseProfile.getAddress())
                .isEmailVerified(responseProfile.isEmailVerified())
                .firstName(responseProfile.getFirstName())
                .lastName(responseProfile.getLastName())
                .createdAt(responseProfile.getCreatedAt())
                .updatedAt(responseProfile.getUpdatedAt())
                .build();
    }
}
