package com.openjoyer.order_service.config;

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
            if (requestTemplate.url().contains("/api/inventory/reserve") ||
                    requestTemplate.url().contains("/api/payment/internal")) {
                String key = env.getProperty("app.internal.secret");
                requestTemplate.header("X-Internal-Request", key);
            }
        };
    }
}