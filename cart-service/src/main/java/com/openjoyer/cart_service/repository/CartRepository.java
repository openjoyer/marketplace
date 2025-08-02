package com.openjoyer.cart_service.repository;

import com.openjoyer.cart_service.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<Cart, String> {
    @Query("{ 'items.?0': { $exists: true } }")
    List<Cart> findByProductId(String productId);

    @Query("{'userId': ?0}")
    Cart findByUserId(String userId);

    void deleteByUserId(String userId);
}
