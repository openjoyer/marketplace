package com.openjoyer.inventoryservice.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class StockEvent {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("stock")
    private int stock;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
}

