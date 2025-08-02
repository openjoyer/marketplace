package com.openjoyer.cart_service.feign_clients;

import com.openjoyer.cart_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "inventory-service",
        configuration = FeignConfig.class
)
public interface InventoryServiceClient {
}
