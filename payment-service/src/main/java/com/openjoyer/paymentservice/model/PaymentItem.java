package com.openjoyer.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentItem {
    @JsonProperty("product")
    private String productId;
    @JsonProperty("seller")
    private String sellerId;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("quantity")
    private int quantity;
}
