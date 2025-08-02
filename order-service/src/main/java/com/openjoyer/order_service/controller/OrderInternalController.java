package com.openjoyer.order_service.controller;

import com.openjoyer.order_service.events.OrderEvent;
import com.openjoyer.order_service.model.OrderItem;
import com.openjoyer.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders/internal")
@RequiredArgsConstructor
public class OrderInternalController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderEvent> getById(@PathVariable("id") String id) {
        OrderEvent orderEvent = orderService.findOrderById(id);
        if (orderEvent == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(orderEvent, HttpStatus.OK);
    }

    @GetMapping("/{id}/items")
    public List<OrderItem> getOrderItems(@PathVariable("id") String id) {
        OrderEvent orderEvent = orderService.findOrderById(id);
        if (orderEvent == null) {
            return null;
        }
        return orderEvent.getItems();
    }
}
