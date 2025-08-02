package com.openjoyer.inventoryservice.feign_clients;

import com.openjoyer.inventoryservice.config.FeignConfig;
import com.openjoyer.inventoryservice.dto.SellerResponseProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductServiceClient {
    @GetMapping("/api/product/seller")
    List<SellerResponseProduct> getSellerProducts(@RequestHeader("X-User-Id") String sellerId);
}
