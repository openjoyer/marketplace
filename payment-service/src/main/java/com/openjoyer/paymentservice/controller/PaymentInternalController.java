package com.openjoyer.paymentservice.controller;

import com.openjoyer.paymentservice.event.OrderEvent;
import com.openjoyer.paymentservice.model.Balance;
import com.openjoyer.paymentservice.model.PaymentStatus;
import com.openjoyer.paymentservice.service.BalanceService;
import com.openjoyer.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/internal")
@RequiredArgsConstructor
@Slf4j
public class PaymentInternalController {
    private final BalanceService balanceService;
    private final PaymentService paymentService;

    @GetMapping("/balance")
    public double userBalance(@RequestParam("id") String userId) {
        Balance balance = balanceService.getBalance(userId);
        return balance.getAmount();
    }

    @PostMapping("/balance")
    public void initBalance(@RequestParam("id") String userId) {
        balanceService.createBalance(userId);
    }

    @PostMapping("/pay")
    public PaymentStatus pay(@RequestParam("id") String userId, @RequestParam("order") String orderId) {
        return paymentService.confirmPayment(userId, orderId);
    }

    @PostMapping("/order-received")
    public void processOrderReceived(@RequestBody OrderEvent orderEvent) {
        paymentService.processOrderReceived(orderEvent);
    }
}
