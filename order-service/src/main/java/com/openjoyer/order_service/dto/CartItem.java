package com.openjoyer.order_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CartItem {
    @JsonProperty("id")
    private String productId;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("price")
    private double price;
    @JsonProperty("seller")
    private String sellerId;
}
