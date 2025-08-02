package com.openjoyer.sellerportalservice.controller;

import com.openjoyer.sellerportalservice.dto.order.SellerOrder;
import com.openjoyer.sellerportalservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class OrderSellerController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestHeader("X-User-Id") String sellerId,
                                       @RequestParam("type") String type) {
        // type: all, archived, in-process
        List<SellerOrder> response = List.of();
        if (type.equals("all")) {
            response = orderService.getAllOrders(sellerId);

        }
        else if (type.equals("in-process")) {
            response = orderService.getOrdersInProcess(sellerId);
        }
        else if (type.equals("archived")) {
            response = orderService.getArchivedOrders(sellerId);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
