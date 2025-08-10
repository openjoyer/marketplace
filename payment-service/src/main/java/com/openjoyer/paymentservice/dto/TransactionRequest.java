package com.openjoyer.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.openjoyer.paymentservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {
    @JsonProperty("user")
    private String userId;
    @JsonProperty("type")
    private TransactionType transactionType;
    @JsonProperty("amount")
    private double amount;

    @JsonProperty("payment")
    private String paymentId;

    public TransactionRequest(String userId, TransactionType transactionType, int amount) {
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
    }
}
