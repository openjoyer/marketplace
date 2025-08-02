package com.openjoyer.product_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.product_service.dto.UserResponseProduct;
import com.openjoyer.product_service.exceptions.ProductNotFoundException;
import com.openjoyer.product_service.model.Product;
import com.openjoyer.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProductService {
    private final ProductRepository productRepository;

    public UserResponseProduct getUserProductById(String id) throws ProductNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("product with id "+ id + " not found"));
        return mapToUserResponseProduct(product);
    }

    public UserResponseProduct getProductByName(String name) throws ProductNotFoundException {
        Product product = productRepository.findByName(name).orElseThrow(() ->
                new ProductNotFoundException("product with name "+ name + " not found"));
        return mapToUserResponseProduct(product);
    }

    public List<UserResponseProduct> getProductByCategory(String category) throws ProductNotFoundException {
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("products with category " + category + " not found");
        }
        return products.stream().map(this::mapToUserResponseProduct).collect(Collectors.toList());
    }

    public List<UserResponseProduct> getProductByPrice(Integer minPrice, Integer maxPrice) throws ProductNotFoundException {
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("products with prices between " + minPrice + " and "+ maxPrice + " not found");
        }
        return products.stream().map(this::mapToUserResponseProduct).collect(Collectors.toList());
    }


    public List<UserResponseProduct> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToUserResponseProduct)
                .collect(Collectors.toList());
    }

    private UserResponseProduct mapToUserResponseProduct(Product product) {
        return UserResponseProduct.builder()
                .name(product.getName())
                .price(product.getPrice())
                .id(product.getId())
                .rating(product.getRating())
                .description(product.getDescription())
                .category(product.getCategory())
                .sellerId(product.getSellerId())
                .build();
    }
}

