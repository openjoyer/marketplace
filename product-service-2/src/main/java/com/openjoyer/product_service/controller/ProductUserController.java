package com.openjoyer.product_service.controller;

import com.openjoyer.product_service.dto.UserResponseProduct;
import com.openjoyer.product_service.exceptions.ProductNotFoundException;
import com.openjoyer.product_service.exceptions.ResponseHandler;
import com.openjoyer.product_service.service.UserProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductUserController {

    private final UserProductService userProductService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String id) {
        UserResponseProduct userResponseProduct;
        try {
            userResponseProduct = userProductService.getUserProductById(id);
            return new ResponseEntity<>(userResponseProduct, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "product not found: " + id,
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String category) {
        try {
            List<UserResponseProduct> response = userProductService.getProductByCategory(category);
            return ResponseEntity.ok(response);
        } catch (ProductNotFoundException e) {
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "product with category " + category + " not found",
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-price")
    public ResponseEntity<?> getProductsByPriceBetween(@RequestParam Integer min, @RequestParam Integer max) {
        try {
            List<UserResponseProduct> products = userProductService.getProductByPrice(min, max);
            return ResponseEntity.ok(products);
        } catch (ProductNotFoundException e) {
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "products with price between " + min + " and " + max + " not found",
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<UserResponseProduct> sellerResponseProducts = userProductService.getAllProducts();
        if(sellerResponseProducts.isEmpty()){
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "no products found",
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(sellerResponseProducts, HttpStatus.OK);
    }


    // ONLY FOR INTERNAL REQUESTS

    @GetMapping("/internal/{id}/price")
    public double getProductPrice(@PathVariable("id") String productId) {
        UserResponseProduct product = userProductService.getUserProductById(productId);
        return product.getPrice();
    }

    @GetMapping("/internal/{id}/seller")
    public String getSellerId(@PathVariable("id") String productId) {
        UserResponseProduct product = userProductService.getUserProductById(productId);
        return product.getSellerId();
    }
}
