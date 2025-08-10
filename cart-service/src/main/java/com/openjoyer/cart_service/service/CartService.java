package com.openjoyer.cart_service.service;

import com.openjoyer.cart_service.dto.CartItemRequest;
import com.openjoyer.cart_service.dto.CartResponse;
import com.openjoyer.cart_service.dto.Inventory;
import com.openjoyer.cart_service.event.PaymentEvent;
import com.openjoyer.cart_service.feign_clients.InventoryServiceClient;
import com.openjoyer.cart_service.feign_clients.ProductServiceClient;
import com.openjoyer.cart_service.model.Cart;
import com.openjoyer.cart_service.model.CartItem;
import com.openjoyer.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

    public CartResponse addItemToCart(CartItemRequest cartItemRequest, String userId) {
        Inventory inventory = inventoryServiceClient.getInventoryInternal(cartItemRequest.getProductId()).getBody();
        if (inventory != null && inventory.getAvailableQuantity() < cartItemRequest.getQuantity()) {
            return null;
        }
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart(userId);
        }
        CartItem item = new CartItem();
        item.setProductId(cartItemRequest.getProductId());

        item.setSellerId(productServiceClient.getSellerId(cartItemRequest.getProductId()));

        double price = productServiceClient.getProductPrice(cartItemRequest.getProductId());
        item.setPrice(price);

        item.setQuantity(cartItemRequest.getQuantity());

        cart.addItem(item);
        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse cartResponse = CartResponse.builder()
                .id(cart.getId())
                .items(cart.getItems())
                .userId(cart.getUserId())
                .totalPrice(cart.getTotalPrice())
                .build();
        return cartResponse;
    }

    public void handleOrderCreated(PaymentEvent paymentEvent) {
        clearCart(paymentEvent.getBuyerId());
    }

    public List<Cart> findByProductId(String productId) {
        return cartRepository.findByProductId(productId);
    }

    public CartItem getCartItem(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null || cart.isEmpty()) {
            return null;
        }
        return cart.getItems().get(productId);
    }

    public Cart removeItemFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId);
        cart.removeItem(productId);
        cartRepository.save(cart);
        return cart;
    }

//    public boolean processCart(String userId) {
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart == null || cart.isEmpty()) {
//            return false;
//        }
//        OrderCartEvent orderCartEvent = OrderCartEvent.builder()
//                .userId(userId)
//                .totalPrice(cart.getTotalPrice())
//                .cartItems(cart.getItems().values().stream()
//                        .map(i ->
//                                new CartItemEvent(
//                                        i.getProductId(),
//                                        i.getQuantity())
//                        )
//                        .toList())
//                .build();
//        try {
//            kafkaProducerService.processOrder(orderCartEvent);
//            return true;
//        } catch (JsonProcessingException e) {
//            log.error("error processing order cart: {}", e.getMessage());
//            return false;
//        }
//    }

    public Cart updateProductQuantity(String userId, String productId, String step) {
        Cart cart = cartRepository.findByUserId(userId);
        if (step.equals("inc")) {
            cart.increaseItemQuantity(productId, 1);
        }
        else {
            cart.decreaseItemQuantity(productId, 1);
        }
        cartRepository.save(cart);
        return cart;
    }

    public Cart clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        cart.removeAll();
        cartRepository.save(cart);
        return cart;
    }

    //TODO ИСправить -> в будущем будет автоматическая инициализация корзины
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart(userId);
            cartRepository.save(cart);
        }
        return mapToCartResponse(cart);
    }
}
