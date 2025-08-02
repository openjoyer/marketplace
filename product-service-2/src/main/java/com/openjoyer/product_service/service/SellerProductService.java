package com.openjoyer.product_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.product_service.dto.RequestProduct;
import com.openjoyer.product_service.dto.SellerResponseProduct;
import com.openjoyer.product_service.events.StockEvent;
import com.openjoyer.product_service.exceptions.ProductNotFoundException;
import com.openjoyer.product_service.model.Product;
import com.openjoyer.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerProductService {

    private final ProductRepository productRepository;
    private final KafkaProducerService kafkaProducerService;

    public SellerResponseProduct createProduct(String sellerId, RequestProduct requestProduct) {
        Product product = Product.builder()
                .name(requestProduct.getName())
                .price(requestProduct.getPrice())
                .description(requestProduct.getDescription())
                .sellerId(sellerId)
                .category(requestProduct.getCategory())
                .rating(0.0)
                .returnsCount(0)
                .sellsCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product result = productRepository.insert(product);
        log.info("product created: {}", product.getId());

        StockEvent stockEvent = StockEvent.builder()
                .productId(product.getId())
                .stock(requestProduct.getStock())
                .timestamp(LocalDateTime.now())
                .build();

        try {
            kafkaProducerService.initProductStock(stockEvent);
            log.info("stock event sent: {}", product.getId());
        } catch (JsonProcessingException e) {
            log.error("stock event failed: {}", e.getMessage());
        }
        return mapToSellerResponseProduct(result);
    }

    public List<SellerResponseProduct> getProductsBySellerId(String sellerId) {
        List<Product> list = productRepository.findBySellerId(sellerId);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        return  list.stream()
                .map(this::mapToSellerResponseProduct)
                .collect(Collectors.toList());
    }

    public SellerResponseProduct getSellerProductById(String id) throws ProductNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("product with id "+ id + " not found"));
        return mapToSellerResponseProduct(product);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
        log.info("product deleted: {}", id);
    }

    public void updateProduct(String id, RequestProduct requestProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product with id "+ id + " not found"));
        product.setPrice(requestProduct.getPrice());
        product.setName(requestProduct.getName());
        product.setDescription(requestProduct.getDescription());
        product.setCategory(requestProduct.getCategory());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        log.info("product updated: {}", id);
    }

    public void updateSellsCount(String id, int count) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("product with id "+ id + " not found"));
        product.setSellsCount(product.getSellsCount() + count);
        productRepository.save(product);
    }


    private SellerResponseProduct mapToSellerResponseProduct(Product product) {
        return SellerResponseProduct.builder()
                .name(product.getName())
                .price(product.getPrice())
                .id(product.getId())
                .rating(product.getRating())
                .description(product.getDescription())
                .category(product.getCategory())
                .sellerId(product.getSellerId())
                .sellsCount(product.getSellsCount())
                .returnsCount(product.getReturnsCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
