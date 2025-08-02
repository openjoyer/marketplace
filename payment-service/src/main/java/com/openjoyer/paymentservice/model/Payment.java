package com.openjoyer.paymentservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document("payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    private String id;

    @Field(name = "order")
    private String orderId;

    @Field(name = "buyer")
    private String buyerId;

    @Field(name = "email")
    private String buyerEmail;

    @Field(name = "items")
    private List<PaymentItem> items;

    @Field(name = "total_amount")
    private double totalAmount;

    @Field(name = "status")
    private PaymentStatus status;

    @Field(name = "timestamp")
    private LocalDateTime timestamp;

    @Field("expires")
    private LocalDateTime expireTimestamp;
}
