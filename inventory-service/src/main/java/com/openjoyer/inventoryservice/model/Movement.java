package com.openjoyer.inventoryservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Movement {
    @JsonProperty("type")
    private MovementType movementType;
    @JsonProperty("user_id")
    private String userId; // FOR RESERVE/ORDER/RETURN
    @JsonProperty("delta")
    private int delta;
    @JsonProperty("date")
    private LocalDate date;


    // FOR SUPPLY
    public Movement(MovementType movementType, LocalDate date, int delta) {
        this.movementType = movementType;
        this.date = date;
        this.delta = delta;
    }
}
