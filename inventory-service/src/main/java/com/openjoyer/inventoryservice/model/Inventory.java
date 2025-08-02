package com.openjoyer.inventoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("inventory")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    @Id
    private String productId;
    private int availableQuantity;
    private int reservedQuantity;
    private List<Movement> movements;

    public void addMovement(Movement movement){
        this.movements.add(movement);
    }
}
