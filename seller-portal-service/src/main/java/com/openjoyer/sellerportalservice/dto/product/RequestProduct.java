package com.openjoyer.sellerportalservice.dto.product;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestProduct {
    private String name;
    private String category;
    private int price;
    private String description;
    private int stock;


    // for updates
    public RequestProduct(String name, String category, Integer price, String description) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }
//    private String photoUrl;
}