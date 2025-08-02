package com.openjoyer.cart_service.dto;

import com.openjoyer.cart_service.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private String id;
    private String userId;
    private Map<String, CartItem> items;
    private double totalPrice;
}
