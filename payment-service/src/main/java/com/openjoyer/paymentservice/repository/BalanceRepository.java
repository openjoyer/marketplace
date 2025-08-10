package com.openjoyer.paymentservice.repository;

import com.openjoyer.paymentservice.model.Balance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends MongoRepository<Balance,String> {
    Optional<Balance> findByUserId(String userId);
}
