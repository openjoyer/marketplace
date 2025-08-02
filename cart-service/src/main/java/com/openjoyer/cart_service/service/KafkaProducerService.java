package com.openjoyer.cart_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

//    TODO В будущем этот топик должен принимать сервис оплаты (и возможно склада)
//    public void processOrder(OrderCartEvent event) throws JsonProcessingException {
//        String json = objectMapper.writeValueAsString(event);
//        kafkaTemplate.send("cart_order_process_topic", json);
//    }
}
