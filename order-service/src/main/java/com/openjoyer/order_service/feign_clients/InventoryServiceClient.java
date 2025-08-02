package com.openjoyer.order_service.feign_clients;

import com.openjoyer.order_service.config.FeignConfig;
import com.openjoyer.order_service.dto.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class
)
public interface InventoryServiceClient {
    @PostMapping("/api/inventory/reserve")
    ResponseEntity<List<CartItem>> reserve(@RequestHeader("X-User-Id") String userId,
                                           @RequestBody List<CartItem> items);
}
