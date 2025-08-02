package com.openjoyer.order_service.repository;

import com.openjoyer.order_service.events.OrderEvent;
import com.openjoyer.order_service.model.Order;
import com.openjoyer.order_service.model.OrderItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    @Query("{'user_id': ?0}")
    List<Order> findByUserId(String userId);

    @Query("{'tracking_number': ?0}")
    Optional<Order> findByTrackingNumber(String trackingNumber);

    boolean existsByTrackingNumber(String trackingNumber);

    void deleteByTrackingNumber(String trackingNumber);

    @Query("{'order_items.seller': ?0}")
    List<Order> findOrderItemsBySellerId(String sellerId);
}
