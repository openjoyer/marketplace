package com.openjoyer.paymentservice.repository;

import com.openjoyer.paymentservice.model.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction,String> {
    Optional<Transaction> findByPaymentId(String paymentId);

    List<Transaction> findByUserId(String userId);

    void deleteByPaymentId(String paymentId);
}
