package com.openjoyer.sellerportalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final OrderService orderService;

    @KafkaListener(
            topics = "order-created",
            groupId = "seller-portal-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void orderCreated(String json, Acknowledgment acknowledgment) {

    }

    @KafkaListener(
            topics = "payment-sucess",
            groupId = "seller-portal-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentSuccess(String json, Acknowledgment acknowledgment) {

    }

    @KafkaListener(
            topics = "payment-cancelled",
            groupId = "seller-portal-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentCanceled(String json, Acknowledgment acknowledgment) {

    }

    @KafkaListener(
            topics = "payment-expired",
            groupId = "seller-portal-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentExpired(String json, Acknowledgment acknowledgment) {

    }
}
