package com.openjoyer.order_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("seller")
    private String sellerId;
    @JsonProperty("price")
    private double price;
}
