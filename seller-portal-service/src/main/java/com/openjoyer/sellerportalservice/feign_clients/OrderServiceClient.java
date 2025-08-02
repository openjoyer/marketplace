package com.openjoyer.sellerportalservice.feign_clients;

import com.openjoyer.sellerportalservice.config.FeignConfig;
import com.openjoyer.sellerportalservice.dto.order.SellerOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "order-service",
        configuration = FeignConfig.class
)
public interface OrderServiceClient {

    @GetMapping("/api/orders/seller/all")
    ResponseEntity<List<SellerOrder>> getAllOrderItems(@RequestHeader("X-User-Id") String sellerId);

    @GetMapping("/api/orders/seller/archived")
    ResponseEntity<List<SellerOrder>> getArchivedOrderItems(@RequestHeader("X-User-Id") String sellerId);

    @GetMapping("/api/orders/seller/in-process")
    ResponseEntity<List<SellerOrder>> getProcessedOrderItems(@RequestHeader("X-User-Id") String sellerId);
}
