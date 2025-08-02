package com.openjoyer.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.order_service.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderCreated(OrderEvent orderEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(orderEvent);
        kafkaTemplate.send("order-created", json);
    }

    public void sendOrder(OrderEvent event) throws JsonProcessingException {
        String jsonEvent = objectMapper.writeValueAsString(event);
        kafkaTemplate.send("order_topic", jsonEvent);
    }

    public void sendOrder(String topic, String key, OrderEvent event) throws JsonProcessingException {
        String jsonEvent = objectMapper.writeValueAsString(event);
        kafkaTemplate.send(topic, key, jsonEvent);
    }
}
