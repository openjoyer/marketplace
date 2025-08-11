package com.openjoyer.paymentservice.controller;

import com.openjoyer.paymentservice.exceptions.BalanceException;
import com.openjoyer.paymentservice.model.Balance;
import com.openjoyer.paymentservice.model.PaymentStatus;
import com.openjoyer.paymentservice.service.BalanceService;
import com.openjoyer.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final BalanceService balanceService;

    @PostMapping("/balance")
    public ResponseEntity<Balance> creditMoney(@RequestHeader("X-User-Id") String userId,
                                               @RequestBody double amount) {
        return new ResponseEntity<>(paymentService.creditMoney(userId, amount), HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<Balance> getBalance(@RequestHeader("X-User-Id") String userId) {
        return new ResponseEntity<>(balanceService.getBalance(userId), HttpStatus.OK);
    }
}
