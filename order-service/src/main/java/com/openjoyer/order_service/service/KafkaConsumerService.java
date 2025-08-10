package com.openjoyer.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.order_service.events.PaymentEvent;
import com.openjoyer.order_service.model.OrderStatus;
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
    private final OrderService orderService;

    @KafkaListener(
            topics = "payment-success",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentSuccess(String json, Acknowledgment ack) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received payment success event: {}", payment.getOrderId());
            orderService.updateStatus(payment.getOrderId().substring(0,8), OrderStatus.PAID);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("error parsing PaymentEvent: {}", e.getMessage());
        }
    }


//    @KafkaListener(
//            topics = "payment-cancelled",
//            groupId = "order-group",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void paymentCancelled(String json, Acknowledgment ack) {
//        try {
//            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
//            log.info("Received payment success event: {}", payment.getOrderId());
//            orderService.updateStatus(payment.getOrderId().substring(0,8), OrderStatus.CANCELED);
//            ack.acknowledge();
//        } catch (JsonProcessingException e) {
//            log.error("error parsing PaymentEvent: {}", e.getMessage());
//        }
//    }

    @KafkaListener(
            topics = "payment-expired",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentExpired(String json, Acknowledgment ack) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received payment success event: {}", payment.getOrderId());
            orderService.updateStatus(payment.getOrderId().substring(0,8), OrderStatus.EXPIRED);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("error parsing PaymentEvent: {}", e.getMessage());
        }
    }
}
