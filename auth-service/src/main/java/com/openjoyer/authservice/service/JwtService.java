package com.openjoyer.authservice.service;

import com.openjoyer.authservice.dto.AuthResponse;
import com.openjoyer.authservice.model.Profile;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final TokenStorageService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;


    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    private String extractId(String jwt) {
        return extractClaim(jwt, e -> e.get("id")).toString();
    }

    public Date extractExpiration(String jwt) {
        return extractClaim(jwt, Claims::getExpiration);
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, expiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String generateToken(Profile user) {
        String token =  Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .claim("role", user.getRole().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
//        tokenService.storeSessionToken(token, user.getId(), expiration);
        return token;
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, String ip) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return (tokenService.hasSession(extractId(token), ip) && (!isTokenExpired(token)));
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public AuthResponse generateJwtResponse(Profile profile) {
        String token = generateToken(profile);
//        String refreshToken = generateRefreshToken(profile);
        return AuthResponse.builder()
                .accessToken(token)
//                .refreshToken(refreshToken)
                .build();
    }
}
