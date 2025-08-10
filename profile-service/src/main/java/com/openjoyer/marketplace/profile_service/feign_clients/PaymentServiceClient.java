package com.openjoyer.marketplace.profile_service.feign_clients;

import com.openjoyer.marketplace.profile_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "payment-service",
        configuration = FeignConfig.class
)
public interface PaymentServiceClient {
    @PostMapping("/api/payment/internal/balance")
    void initBalance(@RequestParam("id") String userId);
}
