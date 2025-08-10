package com.openjoyer.notificationservice.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        @JsonProperty("product_id")
        private String productId;
        @JsonProperty("quantity")
        private int quantity;
        @JsonProperty("price")
        private double price;
        @JsonProperty("seller")
        private String sellerId;
    }

    public enum OrderStatus {
        CREATED,
        PAID,
        PROCESSING,
        PACKED,
        IN_DELIVERY,
        DELIVERED,
        RECEIVED,
        COMPLETED,
        CANCELED,
        REFUNDED,
        EXPIRED
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        @JsonProperty("country")
        private String country;
        @JsonProperty("city")
        private String city;
        @JsonProperty("street")
        private String street;
        @JsonProperty("house_number")
        private String houseNumber;
        @JsonProperty("postal_code")
        private int postalCode;
    }
}

}
