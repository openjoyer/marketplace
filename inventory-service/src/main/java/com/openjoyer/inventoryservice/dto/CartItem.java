package com.openjoyer.inventoryservice.dto;

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
    @JsonProperty("seller")
    private String sellerId;
    @JsonProperty("price")
    private double price;
}