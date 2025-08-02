package com.openjoyer.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GlobalFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${request.internal.secret}")
    private String internalRequestSecret;

    private final List<String> URL_WHITE_LIST = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/product",
            "/api/search",
            "/api/profile/email",
            "/api/auth/confirm-email/proceed",
            "/api/payment/proceed"
    );

    private final List<String> AUTHORIZED_URL_LIST = List.of(
            "/api/auth/validate",
            "/api/auth/confirm-email",
            "/api/auth/logout",
            "/api/product/seller",
            "/api/cart",
            "/api/orders",
            "/api/profile",
            "/api/inventory"
    );
    private final List<String> EMAIL_VERIFIED_URL_LIST = List.of(
            "/api/cart/create-order"
    );
    private final List<String> URL_INTERNAL_LIST = List.of(
            "/api/product/internal",
            "/api/cart/internal",
            "/api/profile/internal",
            "/api/auth/internal",
            "/api/orders/internal",
            "/api/inventory/reserve",
            "/api/product/seller",
            "/api/orders/seller"
    );
    private final String SELLER_PORTAL_URL = "/api/seller";

    private final WebClient authClient;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();

        if (URL_INTERNAL_LIST.stream().anyMatch(url::startsWith)) {
            String internalHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("X-Internal-Request");
            if (!internalRequestSecret.equals(internalHeader)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        }
        if (url.startsWith(SELLER_PORTAL_URL)) {
            String token = extractToken(exchange.getRequest());
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            boolean isTokenValid = authClient.get()
                    .uri("lb://auth-service/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .blockOptional()
                    .orElse(false);
            if (!isTokenValid) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String role = extractRole(token);
            if (!role.equals("ADMIN") && !role.equals("SELLER")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return getEmailVerified(token, exchange, chain);
        }
        else if (URL_WHITE_LIST.stream().anyMatch(url::startsWith)) {
            return chain.filter(exchange);
        }
        else if (EMAIL_VERIFIED_URL_LIST.stream().anyMatch(url::startsWith)){
            String token = extractToken(exchange.getRequest());
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return getEmailVerified(token, exchange, chain);
        }
        else if (AUTHORIZED_URL_LIST.stream().anyMatch(url::startsWith)) {
            String token = extractToken(exchange.getRequest());
            if (token == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return authClient.get()
                    .uri("lb://auth-service/api/auth/validate")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (isValid) {
                            if (url.equals("/api/auth/validate")) {
                                return chain.filter(exchange);
                            }
                            else {
                                ServerHttpRequest mutatedRequest = exchange.getRequest()
                                        .mutate()
                                        .header("X-User-Id", parseUserIdFromToken(token))
                                        .build();
                                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                            }
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    })
                    .onErrorResume(e -> {
                        System.out.println(e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        }
        else {
            return chain.filter(exchange);
        }
    }

    public Mono<Void> getEmailVerified(String token, ServerWebExchange exchange, GatewayFilterChain chain) {
        return authClient.get()
                .uri("lb://auth-service/api/auth/email/verified")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("X-User-Id", parseUserIdFromToken(token))
                .retrieve()
                .bodyToMono(Boolean.class)
                .flatMap(isVerified -> {
                    if (isVerified) {
                        ServerHttpRequest mutatedRequest = exchange.getRequest()
                                .mutate()
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                .header("X-User-Id", parseUserIdFromToken(token))
                                .build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    } else {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAASYDSUFYGDGIUDIUOSUDOUYDOUYOUY"
                        );
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }
                });
    }


    private String extractRole(String token) {
        return extractClaim(token, e -> e.get("role").toString());
    }


    public String parseUserIdFromToken(String token) {
        return extractClaim(token, e -> e.get("id").toString());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String extractToken(org.springframework.http.server.reactive.ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}