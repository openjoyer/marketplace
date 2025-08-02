package com.openjoyer.cart_service.feign_clients;

import com.openjoyer.cart_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductServiceClient {

    @GetMapping("/api/product/{id}")
    ResponseEntity<?> getProductById(@PathVariable("id") String id);

    @GetMapping("/api/product/internal/{id}/price")
    double getProductPrice(@PathVariable("id") String productId);

    @GetMapping("/api/product/internal/{id}/seller")
    String getSellerId(@PathVariable("id") String productId);
}
