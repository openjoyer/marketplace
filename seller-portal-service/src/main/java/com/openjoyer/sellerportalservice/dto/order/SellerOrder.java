package com.openjoyer.sellerportalservice.dto.order;

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

    public enum OrderStatus {
        CREATED,
        PAID,
        PACKED,
        IN_DELIVERY,
        DELIVERED,
        RECEIVED,

        COMPLETED,
        CANCELED,
        REFUNDED,
        EXPIRED
    }
}
