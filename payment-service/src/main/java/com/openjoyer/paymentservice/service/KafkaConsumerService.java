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

    @KafkaListener(topics = "order-created")
    public void processPayment(String json) {
        try {
            OrderEvent order = objectMapper.readValue(json, OrderEvent.class);
            paymentService.create(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
