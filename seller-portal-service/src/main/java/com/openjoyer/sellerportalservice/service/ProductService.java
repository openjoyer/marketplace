package com.openjoyer.sellerportalservice.service;

import com.openjoyer.sellerportalservice.dto.product.RequestProduct;
import com.openjoyer.sellerportalservice.dto.product.SellerResponseProduct;
import com.openjoyer.sellerportalservice.feign_clients.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductServiceClient productServiceClient;

    public SellerResponseProduct createProduct(String sellerId, RequestProduct requestProduct) {
        ResponseEntity<SellerResponseProduct> responseEntity = productServiceClient.createProduct(sellerId, requestProduct);
        SellerResponseProduct responseProduct = responseEntity.getBody();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseProduct;
        } else {
            return null;
        }
    }

    public List<SellerResponseProduct> getAllProducts(String sellerId) {
        List<SellerResponseProduct> responseProducts = productServiceClient.getSellerProducts(sellerId);
        return responseProducts;
    }

    public SellerResponseProduct getProductById(String id) {
        ResponseEntity<SellerResponseProduct> response = productServiceClient.getProduct(id);
        if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
            return null;
        }
        return response.getBody();
    }

    public ResponseEntity<?> updateProduct(String sellerId, RequestProduct requestProduct) {
        ResponseEntity<?> response = productServiceClient.updateProduct(sellerId, requestProduct);
        return response;
    }

    public void deleteProductById(String id) {
        productServiceClient.deleteProduct(id);
    }

    public ResponseEntity<?> getProductStock(String id) {
        return productServiceClient.getProductStock(id);
    }

    public ResponseEntity<?> updateProductStock(String sellerId, int stock) {
        return productServiceClient.updateStock(sellerId, stock);
    }

    public ResponseEntity<?> increaseStock(String id, int stock) {
        return productServiceClient.increaseProductStock(id, stock);
    }

    public ResponseEntity<?> decreaseStock(String id, int stock) {
        return productServiceClient.decreaseProductStock(id, stock);
    }
}
