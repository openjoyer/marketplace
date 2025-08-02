package com.openjoyer.order_service.events;

import com.openjoyer.order_service.model.Address;
import com.openjoyer.order_service.model.OrderItem;
import com.openjoyer.order_service.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String id;
    private String userId;
    private String userEmail;
    private OrderStatus status;
    private double totalAmount;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Address deliveryAddress;
    private String trackingNumber;
    private LocalDate estimatedDeliveryDate;
}
