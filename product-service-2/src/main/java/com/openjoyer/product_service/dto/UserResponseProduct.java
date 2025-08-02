package com.openjoyer.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseProduct {
    private String id;
    private String name;
    private String description;
    private String category;
    private int price;
    private double rating;
    private String sellerId;
}
