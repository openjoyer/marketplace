package com.openjoyer.order_service.controller;

import com.openjoyer.order_service.dto.SellerOrder;
import com.openjoyer.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders/seller")
public class OrderSellerController {
    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<List<SellerOrder>> getAllOrderItems(@RequestHeader("X-User-Id") String sellerId) {
        List<SellerOrder> items = orderService.findAllSellerItems(sellerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<SellerOrder>> getArchivedOrderItems(@RequestHeader("X-User-Id") String sellerId) {
        List<SellerOrder> items = orderService.findArchivedSellerItems(sellerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/in-process")
    public ResponseEntity<List<SellerOrder>> getProcessedOrderItems(@RequestHeader("X-User-Id") String sellerId) {
        List<SellerOrder> items = orderService.findProcessedSellerItems(sellerId);
        return ResponseEntity.ok(items);
    }
}
