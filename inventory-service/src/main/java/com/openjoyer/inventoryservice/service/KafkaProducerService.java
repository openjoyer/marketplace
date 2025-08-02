package com.openjoyer.inventoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

//    public void sendStockOutEvent() {
//        kafkaTemplate.send();
//    }
//
//    public void sendUpdateStockEvent(String productId, int stock) throws JsonProcessingException {
//        try {
//            String json = objectMapper.writeValueAsString(new StockUpdateEvent(productId, stock, LocalDateTime.now()));
//            kafkaTemplate.send("product_stock_updates_topic", productId, json);
//            log.info("sent update stock event: {}", productId);
//        } catch (JsonProcessingException e) {
//            log.error("error mapping StockUpdateEvent: {}", e.getMessage());
//            throw e;
//        }
//    }
}
