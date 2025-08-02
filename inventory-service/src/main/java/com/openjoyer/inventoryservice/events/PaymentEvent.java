package com.openjoyer.inventoryservice.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEvent {
    private String id;

    private String orderId;

    private String buyerId;

    private String buyerEmail;

    private List<PaymentItem> items;

    private double totalAmount;

    private PaymentStatus status;

    private LocalDateTime timestamp;

    private LocalDateTime expireTimestamp;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PaymentItem {
        @JsonProperty("product")
        private String productId;
        @JsonProperty("seller")
        private String sellerId;
        @JsonProperty("amount")
        private double amount;
        @JsonProperty("quantity")
        private int quantity;
    }

    public enum PaymentStatus {
        CREATED,
        SUCCEEDED,
        EXPIRED,
        CANCELLED,
    }
}
