package com.openjoyer.order_service.dto;

import com.openjoyer.order_service.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerOrder {
    private String sellerId;
    private String userId;
    private String orderId;
    private String productId;
    private int quantity;
    private double price;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
}
