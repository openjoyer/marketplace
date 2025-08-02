package com.openjoyer.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private String id;
    private String userId;
    private Map<String, CartItem> items;
    private double totalPrice;

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
