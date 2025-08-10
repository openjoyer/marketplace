package com.openjoyer.order_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.openjoyer.order_service.dto.SellerOrder;
import com.openjoyer.order_service.dto.Cart;
import com.openjoyer.order_service.events.OrderEvent;
import com.openjoyer.order_service.events.PaymentEvent;
import com.openjoyer.order_service.exceptions.ResponseHandler;
import com.openjoyer.order_service.feign_clients.PaymentServiceClient;
import com.openjoyer.order_service.model.Address;
import com.openjoyer.order_service.model.Order;
import com.openjoyer.order_service.model.OrderItem;
import com.openjoyer.order_service.model.OrderStatus;
import com.openjoyer.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.openjoyer.order_service.model.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;
    private final PaymentServiceClient paymentServiceClient;

    private static final List<OrderStatus> cannotCancelStatuses = List.of(
            RECEIVED,
            CANCELED,
            REFUNDED,
            EXPIRED
    );

    public OrderEvent createOrder(String userEmail, Address address, Cart cart) {
        LocalDateTime now = LocalDateTime.now();
        String generatedId = UUID.randomUUID().toString();
        List<OrderItem> orderItems = cart.getItems().values().stream()
                .map(i -> new OrderItem(i.getProductId(), i.getQuantity(), i.getSellerId(), i.getPrice()))
                .toList();

        Order order = Order.builder()
                .id(generatedId)
                .userId(cart.getUserId())
                .deliveryAddress(address)
                .status(OrderStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .estimatedDeliveryDate(calculateDeliveryDate(now))
                .totalAmount(cart.getTotalPrice())
                .trackingNumber(generatedId.substring(0, 8))
                .items(orderItems)
                .build();
        if (orderRepository.existsByTrackingNumber(order.getTrackingNumber())) {
            return null;
        }
        Order savedOrder = orderRepository.save(order);
        log.info("order created: id: {}, trackingNumber: {}", savedOrder.getId(), savedOrder.getTrackingNumber());

        OrderEvent event = mapToOrderEvent(order, userEmail);
        try {
            kafkaProducerService.sendOrderCreated(event);
            log.info("order sent to kafka: id: {}, trackingNumber: {}", savedOrder.getId(), savedOrder.getTrackingNumber());
        } catch (JsonProcessingException e) {
            log.error("Failed to convert order event in JSON: {}", e.getMessage());
        }
        return event;
    }

    /**
     * Mock calculating estimated delivery date
     * Just adds 3-5 days to order date
     * @return LocalDate
     */
    private LocalDate calculateDeliveryDate(LocalDateTime dateTime) {
        int randDays = ThreadLocalRandom.current().nextInt(1, 7);
        return dateTime.toLocalDate().plusDays(randDays);
    }

    public OrderEvent findOrderById(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }
        return mapToOrderEvent(order, null);
    }

    public OrderEvent findByTrackingNumber(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
        if (order == null) {
            return null;
        }
        return mapToOrderEvent(order, null);
    }

    public List<OrderEvent> findByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        return orders.stream().map(e -> mapToOrderEvent(e, null)).collect(Collectors.toList());
    }


    public OrderStatus getOrderStatus(String trackingNumber) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
        if (order == null) {
            return null;
        }
        return order.getStatus();
    }

    public void updateStatus(String trackingNumber, OrderStatus orderStatus) {
        Order order = orderRepository.findByTrackingNumber(trackingNumber).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("order not found");
        }
        order.setStatus(orderStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }


    public void deleteOrder(String trackingNumber) {
        orderRepository.deleteByTrackingNumber(trackingNumber);
    }

    private OrderEvent mapToOrderEvent(Order order, String email) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setId(order.getId());
        orderEvent.setUserId(order.getUserId());
        orderEvent.setUserEmail(email);
        orderEvent.setTotalAmount(order.getTotalAmount());
        orderEvent.setCreatedAt(order.getCreatedAt());
        orderEvent.setUpdatedAt(LocalDateTime.now());
        orderEvent.setItems(order.getItems());
        orderEvent.setStatus(order.getStatus());
        orderEvent.setTrackingNumber(order.getTrackingNumber());
        orderEvent.setDeliveryAddress(order.getDeliveryAddress());
        orderEvent.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        return orderEvent;
    }

    private Order mapToOrder(OrderEvent orderEvent) {
        Order order = new Order();
        order.setId(orderEvent.getId());
        order.setUserId(orderEvent.getUserId());
        order.setDeliveryAddress(orderEvent.getDeliveryAddress());
        order.setTotalAmount(orderEvent.getTotalAmount());
        order.setCreatedAt(orderEvent.getCreatedAt());
        order.setUpdatedAt(orderEvent.getUpdatedAt());
        order.setItems(orderEvent.getItems());
        order.setStatus(orderEvent.getStatus());
        order.setTrackingNumber(orderEvent.getTrackingNumber());
        order.setEstimatedDeliveryDate(orderEvent.getEstimatedDeliveryDate());
        return order;
    }

    public List<SellerOrder> findAllSellerItems(String sellerId) {
        List<Order> orders = orderRepository.findOrderItemsBySellerId(sellerId);
        return getSellerOrders(sellerId, orders);
    }
    
    public List<SellerOrder> findProcessedSellerItems(String sellerId) {
        List<OrderStatus> statuses = List.of(CREATED, PAID, PACKED, IN_DELIVERY, DELIVERED, RECEIVED);
        List<Order> orders = orderRepository.findOrderItemsBySellerId(sellerId).stream()
                .filter(o -> statuses.contains(o.getStatus()))
                .toList();
        return getSellerOrders(sellerId, orders);
    }

    public List<SellerOrder> findArchivedSellerItems(String sellerId) {
        List<OrderStatus> statuses = List.of(RECEIVED, CANCELED, REFUNDED, EXPIRED);
        List<Order> orders = orderRepository.findOrderItemsBySellerId(sellerId).stream()
                .filter(o -> statuses.contains(o.getStatus()))
                .toList();
        return getSellerOrders(sellerId, orders);
    }
    
    private List<SellerOrder> getSellerOrders(String sellerId, List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<SellerOrder> items = new ArrayList<>();
        for (Order order : orders) {
            List<OrderItem> selectedItem = order.getItems().stream()
                    .filter(e -> e.getSellerId().equals(sellerId)).toList();
            for (OrderItem orderItem : selectedItem) {
                SellerOrder sellerOrder = getSellerOrder(order, orderItem);

                items.add(sellerOrder);
            }
        }
        return items;
    }

    private SellerOrder getSellerOrder(Order order, OrderItem orderItem) {
        SellerOrder sellerOrder = new SellerOrder();
        sellerOrder.setUserId(order.getUserId());
        sellerOrder.setOrderId(order.getId());
        sellerOrder.setProductId(orderItem.getProductId());
        sellerOrder.setQuantity(orderItem.getQuantity());
        sellerOrder.setPrice(orderItem.getPrice());
        sellerOrder.setSellerId(orderItem.getSellerId());
        sellerOrder.setOrderStatus(order.getStatus());
        sellerOrder.setCreatedAt(order.getCreatedAt());
        return sellerOrder;
    }

    public boolean cancelOrder(OrderEvent event) {
        if (cannotCancelStatuses.contains(event.getStatus())) {
            return false;
        }
        try {
            kafkaProducerService.sendOrderCanceled(event);
        } catch (JsonProcessingException e) {
            log.error("Failed to send order canceled event", e);
            return false;
        }

        event.setStatus(CANCELED);
        event.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(mapToOrder(event));
        log.info("Order {} has been cancelled", event.getTrackingNumber());
        return true;
    }

    public ResponseEntity<?> payOrder(String userId, String trackingNo) {
        Order order = orderRepository.findByTrackingNumber(trackingNo).orElse(null);
        if (order == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        double userBalance = paymentServiceClient.userBalance(userId);
        if (userBalance <= 0 || userBalance < order.getTotalAmount()) {
            ResponseHandler handler = new ResponseHandler(400,
                    "user balance not enough",
                    LocalDateTime.now());
            return new ResponseEntity<>(handler, HttpStatus.BAD_REQUEST);
        }
        PaymentEvent.PaymentStatus status = paymentServiceClient.pay(userId, order.getId());
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
