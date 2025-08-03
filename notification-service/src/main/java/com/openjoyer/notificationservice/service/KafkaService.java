package com.openjoyer.notificationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openjoyer.notificationservice.dto.NotificationRequest;
import com.openjoyer.notificationservice.events.EmailConfirmationEvent;
import com.openjoyer.notificationservice.events.OrderEvent;
import com.openjoyer.notificationservice.events.PaymentEvent;
import com.openjoyer.notificationservice.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaService {
    private final TemplateEngine templateEngine;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order-created",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreated(String json, Acknowledgment ack) {
        try {
            OrderEvent event = objectMapper.readValue(json, OrderEvent.class);
            log.info("Received order event: {}", event.getId());

            String emailContent = String.format("Dear user, your order has been created! Tracking number: %s",
                    event.getTrackingNumber());

            NotificationRequest request = new NotificationRequest(
                    NotificationType.EMAIL,
                    "ORDER CREATED",
                    emailContent,
                    false,
                    event.getUserEmail()
            );
            notificationService.sendNotification(request);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse OrderEvent: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "payment-created",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumePaymentCreated(String json, Acknowledgment ack) {
        try {
            PaymentEvent payment = objectMapper.readValue(json, PaymentEvent.class);
            log.info("Received payment event: {}", payment.getId());

            Context context = new Context();
            context.setVariable("orderId", payment.getOrderId().substring(0, 8));
            String link = "http://localhost:8070/api/payment/proceed?order=" + payment.getOrderId();
            context.setVariable("paymentLink", link);
            String html = templateEngine.process("email/payment.html", context);

            NotificationRequest request = new NotificationRequest(
                    NotificationType.EMAIL,
                    "ORDER PAYMENT",
                    html,
                    true,
                    payment.getBuyerEmail()
            );
            notificationService.sendNotification(request);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse PaymentEvent: {}", e.getMessage());
        }
    }

    @KafkaListener(
            topics = "order_confirm_email",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeConfirmationEmail(String json, Acknowledgment ack) {
        try {
            EmailConfirmationEvent event = objectMapper.readValue(json, EmailConfirmationEvent.class);
            log.info("Received email confirm event: {}", event.getEmail());

            Context context = new Context();
            context.setVariable("username", event.getEmail());
            String link = "http://localhost:8070/api/auth/confirm-email/proceed?token=" + event.getConfirmationToken();
            context.setVariable("confirmationLink", link);
            String htmlContent = templateEngine.process("email/confirm-email.html", context);

            NotificationRequest request = new NotificationRequest(
                    NotificationType.EMAIL,
                    "EMAIL VERIFICATION",
                    htmlContent,
                    true,
                    event.getEmail()
            );
            notificationService.sendNotification(request);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse EmailConfirmationEvent: {}", e.getMessage());
        }
    }
}
