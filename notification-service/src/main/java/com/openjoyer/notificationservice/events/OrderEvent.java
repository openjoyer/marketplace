package com.openjoyer.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String id;
    private String orderStatus;
    private String trackingNumber;
    private String userEmail;
    private LocalDateTime createdAt;
}
