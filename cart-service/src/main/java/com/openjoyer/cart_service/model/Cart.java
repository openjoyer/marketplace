package com.openjoyer.cart_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

@Document("carts")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    private String id;
    @Field(name = "user_id")
    private String userId;
    @Field(name = "items")
    private Map<String, CartItem> items;
    @Field(name = "total_price")
    private double totalPrice;

    @PersistenceCreator
    public Cart(String userId) {
        this.userId = userId;
        this.items = new HashMap<>();
        this.totalPrice = 0;
    }

    public void addItem(CartItem item) {
        if (items.containsKey(item.getProductId())) {
            int quantity = items.get(item.getProductId()).getQuantity();
            items.get(item.getProductId()).setQuantity(quantity + item.getQuantity());
        } else {
            items.put(item.getProductId(), item);
        }
        recalculateTotalPrice();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void removeAll() {
        items.clear();
        this.totalPrice = 0;
    }

    public void removeItem(String productId) {
        items.remove(productId);
        recalculateTotalPrice();
    }

    public void increaseItemQuantity(String productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        }
        recalculateTotalPrice();
    }

    public void decreaseItemQuantity(String productId, int quantity) {
        if (items.containsKey(productId)) {
            CartItem item = items.get(productId);
            item.setQuantity(item.getQuantity() - quantity);
            if (item.getQuantity() <= 0) {
                items.remove(productId);
            }
        }
        recalculateTotalPrice();
    }


    private void recalculateTotalPrice() {
        this.totalPrice = items.values().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .sum();
    }
}
