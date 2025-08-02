package com.openjoyer.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerResponseProduct {
    private String id;
    private String name;
    private String description;
    private String category;
    private int price;
    private double rating;

    private String sellerId;
    private int sellsCount;
    private int returnsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
