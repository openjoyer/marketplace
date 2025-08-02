package com.openjoyer.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document("orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private String id;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "status")
    private OrderStatus status;

    @Field("order_items")
    private List<OrderItem> items;

    @Field(name = "total_amount")
    private double totalAmount;

    @Field(name = "created_at")
    private LocalDateTime createdAt;

    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    @Field(name = "delivery_address")
    private Address deliveryAddress;

    @Field(name = "tracking_number")
    private String trackingNumber;

    @Field(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

}
