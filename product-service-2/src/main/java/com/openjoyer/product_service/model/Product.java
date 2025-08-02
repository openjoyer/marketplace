package com.openjoyer.product_service.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document("products")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    @Field(name = "name")
    @Indexed(unique = true)
    private String name;
    @Field(name = "description")
    private String description;
    @Field(name = "category")
    private String category;
    @Field(name = "price")
    private int price;
    @Field(name = "rating")
    private double rating;
    @Field("seller_id")
    private String sellerId;

    // for seller only
    @Field(name = "sells_count")
    private int sellsCount;
    @Field(name = "returns_count")
    private int returnsCount;
    @Field(name = "created")
    private LocalDateTime createdAt;
    @Field(name = "updated")
    private LocalDateTime updatedAt;
//    private String photoUrl;
//    private List<ProductQuestion> questions;
}

