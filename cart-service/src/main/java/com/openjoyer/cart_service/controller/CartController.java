package com.openjoyer.cart_service.controller;

import com.openjoyer.cart_service.dto.CartItemRequest;
import com.openjoyer.cart_service.dto.CartResponse;
import com.openjoyer.cart_service.model.Cart;
import com.openjoyer.cart_service.model.CartItem;
import com.openjoyer.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

//    @PostMapping("/create-order")
//    public ResponseEntity<?> processOrder(@RequestHeader("X-User-Id") String userId) {
//        boolean isDone = cartService.processCart(userId);
//        if (isDone) {
//            return ResponseEntity.ok().build();
//        }
//        else {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@RequestHeader("X-User-Id") String userId,
                                                  @RequestBody CartItemRequest cartItem) {
        CartResponse cart = cartService.addItemToCart(cartItem, userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") String userId) {
        CartResponse cart = cartService.getCart(userId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getCartItem(@RequestHeader("X-User-Id") String userId,
                                         @PathVariable("productId") String productId) {
        CartItem cartItem = cartService.getCartItem(userId, productId);
        if (cartItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(cartItem, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProductQuantity(@RequestHeader("X-User-Id") String userId,
                                                   @PathVariable("productId") String productId,
                                                   @RequestParam("step") String step) {
        // step: inc (добавить) / dec (убавить)
        if (!step.equals("inc") && !step.equals("dec")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Cart cart = cartService.updateProductQuantity(userId, productId, step);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping("/{prId}")
    public ResponseEntity<?> removeFromCart(@RequestHeader("X-User-Id") String userId,
                                            @PathVariable("prId") String productId) {
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> clearCart(@RequestHeader("X-User-Id") String userId) {
        Cart cart = cartService.clearCart(userId);
        return new  ResponseEntity<>(cart, HttpStatus.OK);
    }
}
