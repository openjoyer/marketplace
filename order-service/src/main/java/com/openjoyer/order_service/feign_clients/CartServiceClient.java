package com.openjoyer.order_service.feign_clients;

import com.openjoyer.order_service.dto.Cart;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("cart-service")
public interface CartServiceClient {

    @GetMapping("/api/cart")
    ResponseEntity<Cart> getCart(@RequestHeader("X-User-Id") String userId);
}
