package com.openjoyer.inventoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.inventoryservice.events.OrderEvent;
import com.openjoyer.inventoryservice.events.PaymentEvent;
import com.openjoyer.inventoryservice.events.StockEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "product-created",
            groupId = "inventory-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void initProductInventory(String json, Acknowledgment acknowledgment) {
        try {
            StockEvent stockEvent = objectMapper.readValue(json, StockEvent.class);
            log.info("Stock event received: {}", stockEvent);
            inventoryService.initInventory(stockEvent);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("error processing stock event: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "payment-success",
            groupId = "inventory-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentSuccess(String json, Acknowledgment acknowledgment) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received PaymentEvent: {}", payment.getOrderId());

            inventoryService.handlePaymentSuccess(payment);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("json parse error: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "payment-expired",
            groupId = "inventory-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentFailed(String json, Acknowledgment acknowledgment) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received PaymentEvent: {}", payment.getOrderId());

            inventoryService.handlePaymentFailure(payment);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("json parse error: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "order-canceled",
            groupId = "inventory-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void orderCanceled(String json) {
        try {
            OrderEvent orderEvent = objectMapper.readValue(json, OrderEvent.class);
            inventoryService.handleOrderCancel(orderEvent);
        } catch (JsonProcessingException e) {
            log.error("error processing order event: {}", e.getMessage());
        }
    }
}
