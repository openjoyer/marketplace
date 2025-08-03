package com.openjoyer.inventoryservice.controller;

import com.openjoyer.inventoryservice.dto.CartItem;
import com.openjoyer.inventoryservice.dto.InventoryRequest;
import com.openjoyer.inventoryservice.model.Inventory;
import com.openjoyer.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService service;

    @GetMapping("/all")
    public ResponseEntity<?> getAvailable(@RequestHeader("X-User-Id") String sellerId) {
        List<Inventory> inventories = service.getInventoryBySellerId(sellerId);
        return ResponseEntity.ok(inventories);
    }

    @GetMapping
    public ResponseEntity<Inventory> getInventory(@RequestHeader("X-User-Id") String sellerId,
                                          @RequestParam("product") String productId) {
        Inventory inventory = service.getByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    @GetMapping("/internal")
    public ResponseEntity<Inventory> getInventoryInternal(@RequestParam("product") String productId) {
        Inventory inventory = service.getByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public void initProductInventory(@RequestBody InventoryRequest request) {
        service.initInventory(request);
    }

//    @PostMapping("/order")
//    public void processOrder() {
//        service.processOrder();
//    }

    @PostMapping("/internal/reserve")
    public ResponseEntity<List<CartItem>> reserve(@RequestHeader("X-User-Id") String userId,
                                                  @RequestBody List<CartItem> items) {
        Map<Boolean, List<CartItem>> l = service.getReserveItems(items);
        if (l.containsKey(false)) {
            return new ResponseEntity<>(l.get(false), HttpStatus.BAD_REQUEST);
        }
        service.reserve(userId, items);
        return new ResponseEntity<>(l.get(true), HttpStatus.OK);
    }

    @PostMapping("/release")
    public void release() {

    }

    @PostMapping("/return")
    public void createReturn() {

    }

    // Ручное обновление остатков
    @PatchMapping("/update")
    public void update() {

    }
}
