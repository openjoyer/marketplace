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
@Document("operations")
public class Transaction {
    @Id
    private String id;

    @Field(name = "user")
    private String userId;

    @Field(name = "type")
    private TransactionType transactionType;

    @Field(name = "amount")
    private double amount;

    @Field(name = "timestamp")
    private LocalDateTime timestamp;

    public Transaction(String userId, TransactionType transactionType, int amount, LocalDateTime timestamp) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    @Field(name = "payment")
    // can be null (if operation is пополнение баланса)
    private String paymentId;
}
