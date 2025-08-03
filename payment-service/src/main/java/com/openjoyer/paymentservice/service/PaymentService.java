package com.openjoyer.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.paymentservice.event.OrderEvent;
import com.openjoyer.paymentservice.feign_clients.OrderServiceClient;
import com.openjoyer.paymentservice.model.Payment;
import com.openjoyer.paymentservice.model.PaymentItem;
import com.openjoyer.paymentservice.model.PaymentStatus;
import com.openjoyer.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OrderServiceClient orderServiceClient;

    /**
     * Mock payment creating -> sends an email to pay
     */
    public void create(OrderEvent order) {
        if (paymentRepository.existsByOrderId(order.getId())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<PaymentItem> paymentItems = orderServiceClient.getOrderItems(order.getId()).stream()
                .map(i -> new PaymentItem(i.getProductId(), i.getSellerId(), i.getPrice(), i.getQuantity()))
                .toList();

        Payment payment = Payment.builder()
                .orderId(order.getId())
                .buyerId(order.getUserId())
                .buyerEmail(order.getUserEmail())
                .status(PaymentStatus.CREATED)
                .timestamp(now)
                .totalAmount(order.getTotalAmount())
                .items(paymentItems)
                .expireTimestamp(now.plusMinutes(15))
                .build();

        paymentRepository.save(payment);
        log.info("payment created, order: {}", payment.getOrderId());

        try {
            kafkaProducerService.sendPaymentCreated(payment);
            log.info("payment event sent to kafka, order: {}", payment.getOrderId());
        } catch (JsonProcessingException e) {
            log.error("payment event failed, order: {} ({})", payment.getOrderId(), e.getMessage());
        }
    }

    public Payment getByOrderId(String id) {
        return paymentRepository.findByOrderId(id).orElse(null);
    }


    public PaymentStatus confirmPayment(String orderId) {
        Payment payment = getByOrderId(orderId);
        if (payment == null) {
            return null;
        }
        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            return PaymentStatus.CANCELLED;
        }
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            return PaymentStatus.ALREADY_COMPLETED;
        }
        LocalDateTime expired = payment.getExpireTimestamp();
        if (expired.isBefore(LocalDateTime.now())) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            try {
                kafkaProducerService.sendPaymentExpired(payment);
            } catch (JsonProcessingException e) {
                log.error("payment event failed, order: {} ({})", payment.getOrderId(), e.getMessage());
            }
            return PaymentStatus.EXPIRED;
        }
        payment.setExpireTimestamp(null);
        payment.setStatus(PaymentStatus.SUCCEEDED);
        paymentRepository.save(payment);
        try {
            kafkaProducerService.sendPaymentSuccess(payment);
        } catch (JsonProcessingException e) {
            log.error("payment event failed, order: {} ({})", payment.getOrderId(), e.getMessage());
        }
        return PaymentStatus.SUCCEEDED;
    }
}
