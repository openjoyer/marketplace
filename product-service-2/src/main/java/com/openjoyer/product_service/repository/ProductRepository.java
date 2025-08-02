package com.openjoyer.product_service.repository;

import com.openjoyer.product_service.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByCategory(String category);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByCategoryContaining(String categoryPart);

    @Query("{'name': ?0}")
    Optional<Product> findByName(String name);

    @Query("{'price': {$gte: ?0, $lte: ?1}}")
    List<Product> findByPriceBetween(Integer minPrice, Integer maxPrice);

    List<Product> findBySellerId(String sellerId);
}
