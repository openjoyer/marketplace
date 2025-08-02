package com.openjoyer.sellerportalservice.service;

import com.openjoyer.sellerportalservice.dto.order.SellerOrder;
import com.openjoyer.sellerportalservice.feign_clients.OrderServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderServiceClient orderServiceClient;

    public List<SellerOrder> getAllOrders(String sellerId) {
        return orderServiceClient.getAllOrderItems(sellerId).getBody();
    }

    public List<SellerOrder> getArchivedOrders(String sellerId) {
        return orderServiceClient.getArchivedOrderItems(sellerId).getBody();
    }

    public List<SellerOrder> getOrdersInProcess(String sellerId) {
        return orderServiceClient.getProcessedOrderItems(sellerId).getBody();
    }
}
