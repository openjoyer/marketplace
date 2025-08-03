package com.openjoyer.authservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openjoyer.authservice.dto.AuthResponse;
import com.openjoyer.authservice.dto.LoginRequest;
import com.openjoyer.authservice.dto.RegisterRequest;
import com.openjoyer.authservice.dto.ResponseProfile;
import com.openjoyer.authservice.exceptions.ProfileServiceException;
import com.openjoyer.authservice.exceptions.ResponseHandler;
import com.openjoyer.authservice.feign_clients.ProfileServiceClient;
import com.openjoyer.authservice.model.Profile;
import com.openjoyer.authservice.service.AuthService;
import com.openjoyer.authservice.service.EmailConfirmationService;
import com.openjoyer.authservice.service.JwtService;
import com.openjoyer.authservice.service.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenStorageService tokenStorageService;
    private final ProfileServiceClient profileServiceClient;

    @PostMapping("/register")
    public ResponseEntity<?> register(HttpServletRequest request,
                                      @RequestBody RegisterRequest registerRequest) {
        String ip = getClientIp(request);
        log.info("Received request to register: {}", ip);

        try {
            Profile profile = authService.register(registerRequest);
            AuthResponse authResponse = jwtService.generateJwtResponse(profile);
            if (authResponse == null) {
                ResponseHandler responseHandler = new ResponseHandler(400,
                        "Passwords are not the same",
                        LocalDateTime.now());
                return new ResponseEntity<>(responseHandler, HttpStatus.BAD_REQUEST);
            }
            tokenStorageService.storeSessionToken(profile.getId(), ip, authResponse.getAccessToken(), jwtService.getExpiration());
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (ProfileServiceException e) {
            log.error("failed to register({}): {}", ip, e.getMessage());
            ResponseHandler responseHandler = new ResponseHandler(400,
                    e.getMessage(), LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   @RequestBody LoginRequest loginRequest) {
        String ip = getClientIp(request);
        log.info("Received request to login {}", ip);

        Profile profile = authService.authenticate(loginRequest);
        if (profile == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String receivedSessionToken = tokenStorageService.getSession(profile.getId(), ip);
        if (receivedSessionToken == null || receivedSessionToken.isEmpty()) {
            AuthResponse token = jwtService.generateJwtResponse(profile);
            tokenStorageService.storeSessionToken(profile.getId(), ip, token.getAccessToken(), jwtService.getExpiration());
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new AuthResponse(receivedSessionToken), HttpStatus.OK);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<?> sendConfirmationEmail(HttpServletRequest request,
                                                   @RequestHeader("Authorization") String token,
                                                   @RequestHeader("X-User-Id") String userId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String ip = getClientIp(request);
        if (!tokenStorageService.hasSession(userId, ip)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            ResponseEntity<?> responseEntity = profileServiceClient.getProfile(userId);
            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                ResponseHandler h = new ResponseHandler(404, "profile not found: "+responseEntity.getBody(), LocalDateTime.now());
                return new ResponseEntity<>(h, HttpStatus.NOT_FOUND);
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            ResponseProfile profile = mapper.convertValue(responseEntity.getBody(), ResponseProfile.class);
            authService.sendConfirmationEmail(profile);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ProfileServiceException | JsonProcessingException e) {
            log.error("confirmation email sending failed: ", e);
            ResponseHandler responseHandler = new ResponseHandler(
                    400, "confirmation email sending failed: " + e.getMessage(),
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/confirm-email/proceed")
    public ResponseEntity<?> confirmationEmailProceed(@RequestParam("token") String token) {
        Profile user = profileServiceClient.getByConfirmationToken(token);

        if (user == null) {
            return new ResponseEntity<>("Invalid token", HttpStatus.NOT_FOUND);
        }
        if (user.getConfirmationTokenExpiry().isBefore(LocalDateTime.now())) {
            return new  ResponseEntity<>("Token expired", HttpStatus.BAD_REQUEST);
        }

        user.setEmailVerified(true);
        user.setConfirmationToken(null);
        user.setConfirmationTokenExpiry(null);
        user.setUpdatedAt(LocalDateTime.now());
        profileServiceClient.updateProfile(user.getId(), user);

        return ResponseEntity.ok("Email confirmed successfully");
    }

    @GetMapping("/email/verified")
    public ResponseEntity<Boolean> isEmailVerified(@RequestHeader("Authorization") String token,
                                                   @RequestHeader("X-User-Id") String userId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        boolean result = profileServiceClient.isEmailVerified(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request,
                                                 @RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        String ip = getClientIp(request);
        boolean validate = jwtService.isTokenValid(token.substring(7), ip);
        return new ResponseEntity<>(validate, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    @RequestHeader("Authorization") String token,
                                    @RequestHeader("X-User-Id") String userId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String ip = getClientIp(request);
        token = token.substring(7);
        if (!tokenStorageService.hasSession(userId, ip)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        tokenStorageService.invalidateToken(userId, ip);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutAll(HttpServletRequest request,
                                       @RequestHeader("Authorization") String token,
                                       @RequestHeader("X-User-Id") String userId) {
        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String ip = getClientIp(request);
        token = token.substring(7);
        if (!tokenStorageService.hasSession(userId, ip)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        tokenStorageService.invalidateAllUserTokens(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Если IP содержит несколько адресов (через запятую), берем первый
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }

        return ipAddress;
    }
}
