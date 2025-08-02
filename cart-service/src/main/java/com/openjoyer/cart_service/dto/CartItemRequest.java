package com.openjoyer.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemRequest {
    @JsonProperty("product_id")
    private String productId;
    @JsonProperty("quantity")
    private int quantity;
    @JsonProperty("seller")
    private String sellerId;
}
