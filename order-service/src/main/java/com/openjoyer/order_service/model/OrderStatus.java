package com.openjoyer.order_service.model;

public enum OrderStatus {
    CREATED,
    PAID,
    PACKED,
    IN_DELIVERY,
    DELIVERED,

    RECEIVED,
    CANCELED,
    REFUNDED,
    EXPIRED
}
