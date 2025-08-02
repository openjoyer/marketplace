package com.openjoyer.sellerportalservice.controller;

import com.openjoyer.sellerportalservice.dto.ResponseHandler;
import com.openjoyer.sellerportalservice.dto.product.RequestProduct;
import com.openjoyer.sellerportalservice.dto.product.SellerResponseProduct;
import com.openjoyer.sellerportalservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/seller/product")
@RequiredArgsConstructor
@Slf4j
public class ProductSellerController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> addProduct(@RequestHeader("X-User-Id") String sellerId,
                           @RequestBody RequestProduct requestProduct) {
        SellerResponseProduct response = productService.createProduct(sellerId, requestProduct);
        if (response == null) {
            ResponseHandler h = new ResponseHandler(400, "something went wrong", LocalDateTime.now());
            return new ResponseEntity<>(h,  HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts(@RequestHeader("X-User-Id") String sellerId) {
        List<SellerResponseProduct> response = productService.getAllProducts(sellerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") String id) {
        SellerResponseProduct response = productService.getProductById(id);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProductById(@PathVariable("id") String id, @RequestBody RequestProduct requestProduct) {
        ResponseEntity<?> res = productService.updateProduct(id, requestProduct);
        return res;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProductById(@PathVariable("id") String id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<?> getProductStock(@PathVariable("id")  String id) {
        return productService.getProductStock(id);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<?> updateProductStock(@PathVariable("id") String id, @RequestParam("amount") int stock) {
        return productService.updateProductStock(id, stock);
    }

    @PutMapping("/{id}/stock/increase")
    public ResponseEntity<?> increaseStock(@PathVariable("id") String id, @RequestParam("amount") int amount) {
        return productService.increaseStock(id, amount);
    }

    @PutMapping("/{id}/stock/decrease")
    public ResponseEntity<?> decreaseStock(@PathVariable("id") String id, @RequestParam("amount") int amount) {
        return productService.decreaseStock(id, amount);
    }
}
