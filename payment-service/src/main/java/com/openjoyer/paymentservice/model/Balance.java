package com.openjoyer.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("balances")
public class Balance {
    @Id
    private String userId;
    @Field(name = "amount")
    private double amount;
    @Field(name = "updated_at")
    private LocalDateTime updatedAt;

    public void increase(double amount) {
        double updatedAmount = this.amount + amount;
        setAmount(updatedAmount);
    }

    public void decrease(double amount) {
        double updatedAmount = this.amount - amount;
        setAmount(updatedAmount);
    }
}
