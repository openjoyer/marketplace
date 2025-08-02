package com.openjoyer.cart_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity && Double.compare(price, cartItem.price) == 0 && Objects.equals(productId, cartItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, price);
    }
}
