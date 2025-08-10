package com.openjoyer.paymentservice.service;

import com.openjoyer.paymentservice.exceptions.BalanceException;
import com.openjoyer.paymentservice.model.Balance;
import com.openjoyer.paymentservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public Balance createBalance(String userId) {
        Balance balance = Balance.builder()
                .userId(userId)
                .amount(0.0)
                .updatedAt(LocalDateTime.now())
                .build();
        return balanceRepository.save(balance);
    }

    public Balance getBalance(String userId) {
        return balanceRepository.findById(userId).orElse(null);
    }

    public Balance incrementBalance(String userId, double amount) {
        Balance balance = getBalance(userId);
        if (balance == null) {
            return null;
        }
        balance.increase(amount);
        return balanceRepository.save(balance);
    }

    public Balance decrementBalance(String userId, double amount) throws BalanceException {
        Balance balance = getBalance(userId);
        if (balance == null) {
            return null;
        }
        if (balance.getAmount() < amount) {
            throw new BalanceException("insufficient balance");
        }
        balance.decrease(amount);
        return balanceRepository.save(balance);
    }

    public Balance clearBalance(String userId) {
        Balance balance = getBalance(userId);
        balance.setAmount(0);
        return balanceRepository.save(balance);
    }
}
