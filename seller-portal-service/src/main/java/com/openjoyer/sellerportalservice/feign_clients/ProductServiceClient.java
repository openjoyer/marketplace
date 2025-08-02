package com.openjoyer.sellerportalservice.feign_clients;

import com.openjoyer.sellerportalservice.config.FeignConfig;
import com.openjoyer.sellerportalservice.dto.product.RequestProduct;
import com.openjoyer.sellerportalservice.dto.product.SellerResponseProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "product-service",
        configuration = FeignConfig.class
)
public interface ProductServiceClient {

    @PostMapping("/api/product/seller")
    ResponseEntity<SellerResponseProduct> createProduct(@RequestHeader("X-User-Id") String sellerId,
                                                  @RequestBody RequestProduct requestProduct);
    @GetMapping("/api/product/seller")
    List<SellerResponseProduct> getSellerProducts(@RequestHeader("X-User-Id") String sellerId);

    @GetMapping("/api/product/seller/{id}")
    ResponseEntity<SellerResponseProduct> getProduct(@PathVariable("id") String id);

    @PutMapping("/api/product/seller/{id}")
    ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody RequestProduct requestProduct);

    @DeleteMapping("/api/product/seller/{id}")
    void deleteProduct(@PathVariable("id") String id);

    @PutMapping("/api/product/seller/{id}/stock")
    ResponseEntity<?> updateStock(@PathVariable("id") String id, @RequestParam("amount") int stock);

    @GetMapping("/api/product/seller/{id}/stock")
    ResponseEntity<?> getProductStock(@PathVariable("id") String productId);

    @PutMapping("/api/product/seller/{id}/stock/decrease")
    ResponseEntity<?> decreaseProductStock(@PathVariable("id") String productId, @RequestParam("amount") int decreaseAmount);

    @PutMapping("/api/product/seller/{id}/stock/increase")
    ResponseEntity<?> increaseProductStock(@PathVariable("id") String productId, @RequestParam("amount") int increaseAmount);
}
