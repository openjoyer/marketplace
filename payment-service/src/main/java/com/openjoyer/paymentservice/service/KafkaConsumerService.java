package com.openjoyer.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.paymentservice.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void processPayment(String json) {
        try {
            OrderEvent order = objectMapper.readValue(json, OrderEvent.class);
            paymentService.create(order);
        } catch (JsonProcessingException e) {
            log.error("error processing order event: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "order-canceled",
            groupId = "payment-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void orderCanceled(String json) {
        try {
            OrderEvent orderEvent = objectMapper.readValue(json, OrderEvent.class);
            paymentService.cancelAction(orderEvent);
        } catch (JsonProcessingException e) {
            log.error("error processing order event: {}", e.getMessage());
        }
    }
}
