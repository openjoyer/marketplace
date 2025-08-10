package com.openjoyer.cart_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.cart_service.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final CartService cartService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "payment-success",
            groupId = "cart-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void paymentSuccess(String json, Acknowledgment ack) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received Payment Event: {}", payment.getId());

            cartService.handleOrderCreated(payment);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse payment event: {}", e.getMessage());
        }
    }

//    @KafkaListener(
//            topics = "product_stock_updates_topic",
//            groupId = "cart-group",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void updateItemStock(String json, Acknowledgment ack) {
//        try {
//            StockUpdateEvent event = objectMapper.readValue(json, StockUpdateEvent.class);
//            log.info("Received update item stock update request: {}", event.getProductId());
//
//            String productId = event.getProductId();
//            List<Cart> carts = cartService.findByProductId(productId);
//
//            carts.forEach(cart -> {
//                CartItem item = cart.getItems().get(productId);
//                if (item != null) {
//                    cart.adjustItemMaxQuantity(productId, event.getNewStock());
//                }
//                cartService.saveCart(cart);
//            });
//            ack.acknowledge();
//        } catch (JsonProcessingException e) {
//            log.error("error processing json: {}", e.getMessage());
//        }
//    }

//    @KafkaListener(
//            topics = "order_topic",
//            groupId = "cart-group",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void consumeOrderEvent(String json, Acknowledgment ack) {
//        try {
//            OrderEvent event = objectMapper.readValue(json, OrderEvent.class);
//            cartService.processOrderCreation(event);
//            log.info("Updated cart items: {}", event.getTrackingNumber());
//            ack.acknowledge();
//        } catch (JsonProcessingException e) {
//            log.error("error processing json: {}", e.getMessage());
//        }
//    }
}
