package com.openjoyer.product_service.controller;

import com.openjoyer.product_service.dto.RequestProduct;
import com.openjoyer.product_service.dto.SellerResponseProduct;
import com.openjoyer.product_service.exceptions.ProductNotFoundException;
import com.openjoyer.product_service.exceptions.ResponseHandler;
import com.openjoyer.product_service.service.SellerProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/product/seller")
@RequiredArgsConstructor
public class ProductSellerController {

    private final SellerProductService productService;

    @PostMapping
    public ResponseEntity<SellerResponseProduct> createProduct(@RequestHeader("X-User-Id") String sellerId,
                                                               @RequestBody RequestProduct requestProduct) {
        SellerResponseProduct response = productService.createProduct(sellerId, requestProduct);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerResponseProduct> getProduct(@PathVariable("id") String id) {
        try {
            SellerResponseProduct response = productService.getSellerProductById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public List<SellerResponseProduct> getSellerProducts(@RequestHeader("X-User-Id") String sellerId) {
        return productService.getProductsBySellerId(sellerId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody RequestProduct requestProduct) {
        try {
            productService.updateProduct(id, requestProduct);
            return ResponseEntity.ok().build();
        } catch (ProductNotFoundException e) {
            ResponseHandler responseHandler = new ResponseHandler(404,
                    "product not found: " + id,
                    LocalDateTime.now());
            return new ResponseEntity<>(responseHandler, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
    }

//    @PutMapping("/{id}/stock")
//    public ResponseEntity<?> updateStock(@PathVariable("id") String id,
//                                         @RequestParam("amount") int stock) {
//        try {
//            productService.updateStock(id, stock);
//            return ResponseEntity.ok().build();
//        } catch (ProductNotFoundException e) {
//            ResponseHandler h = new ResponseHandler(404,
//                    "product not found: " + id,
//                    LocalDateTime.now());
//            return new ResponseEntity<>(h, HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/{id}/stock")
//    public ResponseEntity<?> getProductStock(@PathVariable("id") String productId) {
//        try {
//            SellerResponseProduct product = productService.getSellerProductById(productId);
//            return new ResponseEntity<>(product, HttpStatus.OK);
//        } catch (ProductNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping("/{id}/stock/decrease")
//    public ResponseEntity<?> decreaseProductStock(@PathVariable("id") String productId, @RequestParam("amount") int decreaseAmount) {
//        try {
//            int newStock = productService.decreaseStock(productId, decreaseAmount);
//            return new ResponseEntity<>(newStock, HttpStatus.OK);
//        } catch (ProductNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PutMapping("/{id}/stock/increase")
//    public ResponseEntity<?> increaseProductStock(@PathVariable("id") String productId, @RequestParam("amount") int increaseAmount) {
//        try {
//            int newStock = productService.increaseStock(productId, increaseAmount);
//            return new ResponseEntity<>(newStock, HttpStatus.OK);
//        } catch (ProductNotFoundException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
