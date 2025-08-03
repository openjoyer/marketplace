package com.openjoyer.cart_service.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final Environment env;

    @Bean
    public RequestInterceptor internalAuthInterceptor() {
        return requestTemplate -> {
            if (requestTemplate.url().contains("/api/product/internal") ||
                    requestTemplate.url().contains("/api/inventory/internal")) {
                String key = env.getProperty("app.internal.secret");
                requestTemplate.header("X-Internal-Request", key);
            }
        };
    }
}