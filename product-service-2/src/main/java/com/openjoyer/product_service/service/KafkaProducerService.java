package com.openjoyer.product_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.product_service.events.StockEvent;
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
    private static final String PRODUCT_TOPIC = "product-created";

    public void initProductStock(StockEvent stockEvent) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(stockEvent);
        kafkaTemplate.send(PRODUCT_TOPIC, json);
    }
}
