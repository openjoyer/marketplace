package com.openjoyer.paymentservice.controller;

import com.openjoyer.paymentservice.model.PaymentStatus;
import com.openjoyer.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/proceed")
    public ResponseEntity<String> paymentProceed(@RequestParam("order") String orderId) {
        PaymentStatus status = paymentService.confirmPayment(orderId);
        if (status == PaymentStatus.SUCCEEDED) {
            log.info("Payment successful: {}", orderId);
        }
        else if (status == PaymentStatus.CANCELLED) {
            log.info("Payment cancelled: {}", orderId);
        }
        else if (status == PaymentStatus.EXPIRED) {
            log.info("Payment expired: {}", orderId);
        }
        else if (status == PaymentStatus.ALREADY_COMPLETED) {
            log.info("Payment already completed: {}", orderId);
        }
        else {
            log.error("Payment is null: {}", orderId);
        }
        return new ResponseEntity<>(status.toString(), HttpStatus.OK);
    }
}
