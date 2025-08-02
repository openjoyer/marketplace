package com.openjoyer.product_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.product_service.events.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ObjectMapper objectMapper;
    private final SellerProductService sellerProductService;

    @KafkaListener(
            topics = "payment-success",
            groupId = "product-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentSuccess(String json, Acknowledgment ack) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received payment success event: {}", payment.getOrderId());
            for (PaymentEvent.PaymentItem item : payment.getItems()) {
                sellerProductService.updateSellsCount(item.getProductId(), item.getQuantity());
            }
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("error parsing PaymentEvent: {}", e.getMessage());
        }
    }
}
