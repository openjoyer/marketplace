package com.openjoyer.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.paymentservice.dto.TransactionRequest;
import com.openjoyer.paymentservice.event.OrderEvent;
import com.openjoyer.paymentservice.exceptions.BalanceException;
import com.openjoyer.paymentservice.feign_clients.OrderServiceClient;
import com.openjoyer.paymentservice.model.*;
import com.openjoyer.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OrderServiceClient orderServiceClient;
    private final BalanceService balanceService;
    private final TransactionService transactionService;

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

//        try {
//            kafkaProducerService.sendPaymentCreated(payment);
//            log.info("payment event sent to kafka, order: {}", payment.getOrderId());
//        } catch (JsonProcessingException e) {
//            log.error("payment event failed, order: {} ({})", payment.getOrderId(), e.getMessage());
//        }
    }

    @Scheduled(fixedRate = 10000)
    public void releaseExpiredReservations() {
        List<Payment> expiredPayments = paymentRepository.findByStatusAndExpireTimestampBefore(
                        PaymentStatus.CREATED,
                        LocalDateTime.now()
        );
        expiredPayments.forEach(payment -> {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            try {
                kafkaProducerService.sendPaymentExpired(payment);
            } catch (JsonProcessingException e) {
                log.error("payment event failed, order: {} ({})", payment.getOrderId(), e.getMessage());
            }
        });
    }

    public Payment getByOrderId(String id) {
        return paymentRepository.findByOrderId(id).orElse(null);
    }


    public PaymentStatus confirmPayment(String userId, String orderId) {
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
        if (payment.getStatus() == PaymentStatus.EXPIRED) {
            return PaymentStatus.EXPIRED;
        }

        try {
            balanceService.decrementBalance(userId, payment.getTotalAmount());
        } catch (BalanceException e) {
            return PaymentStatus.INSUFFICIENT_BALANCE;
        }

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .paymentId(payment.getId())
                .userId(userId)
                .amount(payment.getTotalAmount())
                .transactionType(TransactionType.USER_HOLD)
                .build();
        transactionService.create(transactionRequest);

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

    public void cancelAction(OrderEvent orderEvent) {
        Payment payment = getByOrderId(orderEvent.getId());
        if (payment == null) {
            return;
        }
        if (payment.getStatus() == PaymentStatus.CREATED) {
            payment.setStatus(PaymentStatus.CANCELLED);
        }
        if (payment.getStatus() == PaymentStatus.SUCCEEDED ||  payment.getStatus() == PaymentStatus.ALREADY_COMPLETED) {
            payment.setStatus(PaymentStatus.RETURNED);
            createUserReturn(orderEvent);
        }
        else {
            return;
        }
        paymentRepository.save(payment);
    }

    public void returnAction(OrderEvent orderEvent) {

    }

    private void createUserReturn(OrderEvent orderEvent) {
        balanceService.incrementBalance(orderEvent.getUserId(), orderEvent.getTotalAmount());

        TransactionRequest transactionRequest = TransactionRequest.builder()
                .userId(orderEvent.getUserId())
                .amount(orderEvent.getTotalAmount())
                .transactionType(TransactionType.USER_RETURN)
                .build();
        transactionService.create(transactionRequest);
    }
    private void createSellerReturn(OrderEvent orderEvent) {
    }

}
