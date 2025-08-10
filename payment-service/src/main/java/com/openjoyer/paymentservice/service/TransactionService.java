package com.openjoyer.paymentservice.service;

import com.openjoyer.paymentservice.dto.TransactionRequest;
import com.openjoyer.paymentservice.model.Transaction;
import com.openjoyer.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public void create(TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .transactionType(request.getTransactionType())
                .paymentId(request.getPaymentId())
                .userId(request.getUserId())
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }

    public void delete(Transaction transaction) {
        transactionRepository.delete(transaction);
    }

    public void deleteById(String id) {
        transactionRepository.deleteById(id);
    }

    public void deleteByPaymentId(String paymentId) {
        transactionRepository.deleteByPaymentId(paymentId);
    }

    public List<Transaction> findAll(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction findById(String id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Transaction findByPaymentId(String paymentId) {
        return transactionRepository.findByPaymentId(paymentId).orElse(null);
    }
}
