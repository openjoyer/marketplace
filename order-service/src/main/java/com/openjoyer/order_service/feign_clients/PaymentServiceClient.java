package com.openjoyer.order_service.feign_clients;

import com.openjoyer.order_service.config.FeignConfig;
import com.openjoyer.order_service.events.PaymentEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "payment-service",
        configuration = FeignConfig.class
)
public interface PaymentServiceClient {

    @GetMapping("/api/payment/internal/balance")
    double userBalance(@RequestParam("id") String userId);

    @PostMapping("/api/payment/internal/pay")
    PaymentEvent.PaymentStatus pay(@RequestParam("id") String userId, @RequestParam("order") String orderId);
}
