package com.openjoyer.paymentservice.feign_clients;

import com.openjoyer.paymentservice.config.FeignConfig;
import com.openjoyer.paymentservice.event.OrderEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "order-service",
        configuration = FeignConfig.class
)
public interface OrderServiceClient {

    @GetMapping("/api/orders/internal/{id}")
    ResponseEntity<OrderEvent> getById(@PathVariable("id") String id);

    @GetMapping("/api/orders/internal/{id}/items")
    List<OrderEvent.OrderItem> getOrderItems(@PathVariable("id") String id);
}
