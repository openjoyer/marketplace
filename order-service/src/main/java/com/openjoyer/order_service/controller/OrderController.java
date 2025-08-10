package com.openjoyer.order_service.controller;

import com.openjoyer.order_service.dto.Cart;
import com.openjoyer.order_service.dto.CartItem;
import com.openjoyer.order_service.events.OrderEvent;
import com.openjoyer.order_service.exceptions.ResponseHandler;
import com.openjoyer.order_service.feign_clients.CartServiceClient;
import com.openjoyer.order_service.feign_clients.InventoryServiceClient;
import com.openjoyer.order_service.feign_clients.PaymentServiceClient;
import com.openjoyer.order_service.feign_clients.ProfileServiceClient;
import com.openjoyer.order_service.model.Address;
import com.openjoyer.order_service.model.OrderStatus;
import com.openjoyer.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final CartServiceClient cartServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final ProfileServiceClient profileServiceClient;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestHeader("X-User-Id") String userId) {
        Cart cart = cartServiceClient.getCart(userId).getBody();
        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<CartItem> cartItems = cart.getItems().values().stream().toList();
        ResponseEntity<List<CartItem>> inventoryResponse = inventoryServiceClient.reserve(userId, cartItems);
        if (inventoryResponse.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(inventoryResponse.getBody(), inventoryResponse.getStatusCode());
        }
        String email = profileServiceClient.getProfileEmail(userId);
        Address address = profileServiceClient.getAddress(userId);
        OrderEvent orderEvent = orderService.createOrder(email, address, cart);
        if (orderEvent == null) {
            ResponseHandler handler = new ResponseHandler(500,
                    "order with tracking No. already exists",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(orderEvent, HttpStatus.CREATED);

    }

    @PostMapping("/pay")
    public ResponseEntity<?> payOrder(@RequestHeader("X-User-Id") String userId,
                                      @RequestParam("track") String trackingNo) {
        return orderService.payOrder(userId, trackingNo);
    }

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("X-User-Id") String userId,
                                         @RequestParam("track") String trackingNo) {
        if (trackingNo.length() > 8) {
            // Костыль для приведения айдишника к трек-номеру
            trackingNo = trackingNo.substring(0, 8);
        }
        OrderEvent order = orderService.findByTrackingNumber(trackingNo);
        if (order == null) {
            ResponseHandler handler = new ResponseHandler(404,
                    "order not found: "+ trackingNo,
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.NOT_FOUND);
        } else if (!order.getUserId().equals(userId)) {
            ResponseHandler handler = new ResponseHandler(403,
                    "access denied: you can not access others' orders",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.FORBIDDEN);
        }
        boolean isDone = orderService.cancelOrder(order);
    }



    @GetMapping("/{track}")
    public ResponseEntity<?> getOrder(@RequestHeader("X-User-Id") String userId,
                                      @PathVariable("track") String trackingNumber) {
        if (trackingNumber.length() > 8) {
            // Костыль для приведения айдишника к трек-номеру
            trackingNumber = trackingNumber.substring(0, 8);
        }
        OrderEvent order = orderService.findByTrackingNumber(trackingNumber);
        if (order == null) {
            ResponseHandler handler = new ResponseHandler(404,
                    "order not found: "+ trackingNumber,
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.NOT_FOUND);
        } else if (!order.getUserId().equals(userId)) {
            ResponseHandler handler = new ResponseHandler(403,
                    "access denied: you can not access others' orders",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/{track}/status")
    public ResponseEntity<?> getOrderStatus(@RequestHeader("X-User-Id") String userId,
                                            @PathVariable("track") String trackingNumber) {
        OrderEvent order = orderService.findByTrackingNumber(trackingNumber);
        if (order == null) {
            return new ResponseEntity<>(new ResponseHandler(404,
                    "order not found: " + trackingNumber,
                    LocalDateTime.now()), HttpStatus.NOT_FOUND);
        } else if (!order.getUserId().equals(userId)) {
            ResponseHandler handler = new ResponseHandler(403,
                    "you can not access other's order",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.FORBIDDEN);
        }
        OrderStatus orderStatus = order.getStatus();
        return new ResponseEntity<>(orderStatus, HttpStatus.OK);
    }

    @PutMapping("/{track}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("track") String trackingNumber,
                                               @RequestBody OrderStatus orderStatus) {
        try {
            orderService.updateStatus(trackingNumber, orderStatus);
        } catch (Exception ex) {
            ResponseHandler handler = new ResponseHandler(404,
                    "order not found: " + trackingNumber,
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{track}")
    public ResponseEntity<?> deleteOrder(@PathVariable("track") String trackingNumber) {
        orderService.deleteOrder(trackingNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getOrders(@RequestHeader("X-User-Id") String userId) {
        List<OrderEvent> orders = orderService.findByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
