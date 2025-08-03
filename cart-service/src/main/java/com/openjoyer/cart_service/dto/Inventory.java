package com.openjoyer.cart_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    private String productId;
    private int availableQuantity;
    private int reservedQuantity;
    private List<Movement> movements;

    public void addMovement(Movement movement){
        this.movements.add(movement);
    }

    @Data
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Movement {
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

    public enum MovementType {
        SUPPLY,
        RESERVE,
        RESERVE_CANCEL,
        ORDER,
        RETURN
    }
}

