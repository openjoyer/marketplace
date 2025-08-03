package com.openjoyer.cart_service.feign_clients;

import com.openjoyer.cart_service.config.FeignConfig;
import com.openjoyer.cart_service.dto.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class
)
public interface InventoryServiceClient {
    @GetMapping("/api/inventory/internal")
    ResponseEntity<Inventory> getInventoryInternal(@RequestParam("product") String productId);
}
