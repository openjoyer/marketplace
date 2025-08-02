package com.openjoyer.inventoryservice.service;

import com.openjoyer.inventoryservice.dto.CartItem;
import com.openjoyer.inventoryservice.dto.InventoryRequest;
import com.openjoyer.inventoryservice.dto.SellerResponseProduct;
import com.openjoyer.inventoryservice.events.PaymentEvent;
import com.openjoyer.inventoryservice.events.StockEvent;
import com.openjoyer.inventoryservice.feign_clients.ProductServiceClient;
import com.openjoyer.inventoryservice.model.Inventory;
import com.openjoyer.inventoryservice.model.Movement;
import com.openjoyer.inventoryservice.model.MovementType;
import com.openjoyer.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductServiceClient productServiceClient;

    public List<Inventory> getInventoryBySellerId(@RequestHeader("X-User-Id") String sellerId) {
        List<SellerResponseProduct> sellerResponseProducts = productServiceClient.getSellerProducts(sellerId);
        List<Inventory> inventories = new ArrayList<>();
        for (SellerResponseProduct sellerResponseProduct : sellerResponseProducts) {
            Inventory inventory = inventoryRepository.findByProductId(sellerResponseProduct.getId()).orElse(null);
            if (inventory != null) {
                inventories.add(inventory);
            }
        }
        return inventories;
    }


    public Map<Boolean, List<CartItem>> getReserveItems(List<CartItem> items) {
        List<CartItem> insufficientItems = new ArrayList<>();
        List<CartItem> itemsToReserve = new ArrayList<>();
        Map<Boolean, List<CartItem>> map = new HashMap<>();

        for (CartItem item : items) {
            Inventory i =  inventoryRepository.findByProductId(item.getProductId()).orElse(null);
            if (i == null || i.getAvailableQuantity() < item.getQuantity()) {
                insufficientItems.add(item);
            }
            else {
                itemsToReserve.add(item);
            }
        }
        if (!insufficientItems.isEmpty()) {
            map.put(false, insufficientItems);
        }
        else {
            map.put(true, itemsToReserve);
        }
        return map;
    }

    public void reserve(String userId, List<CartItem> itemsToReserve) {
        for (CartItem item : itemsToReserve) {
            Inventory i =  inventoryRepository.findByProductId(item.getProductId()).orElse(null);
            i.setAvailableQuantity(i.getAvailableQuantity() - item.getQuantity());
            i.setReservedQuantity(i.getReservedQuantity() + item.getQuantity());
            Movement movement = Movement.builder()
                    .movementType(MovementType.RESERVE)
                    .date(LocalDate.now())
                    .userId(userId)
                    .delta(item.getQuantity())
                    .build();
            i.addMovement(movement);
            inventoryRepository.save(i);
        }
    }

    public void handlePaymentSuccess(PaymentEvent paymentEvent) {
        for (PaymentEvent.PaymentItem item : paymentEvent.getItems()) {
            Inventory i =  inventoryRepository.findByProductId(item.getProductId()).orElse(null);
            if (i != null) {
                i.setReservedQuantity(i.getReservedQuantity() - item.getQuantity());
                Movement itemMovement = Movement.builder()
                        .date(LocalDate.now())
                        .userId(paymentEvent.getBuyerId())
                        .delta(item.getQuantity())
                        .movementType(MovementType.ORDER)
                        .build();
                i.addMovement(itemMovement);
                inventoryRepository.save(i);
            }
        }
    }

    public void handlePaymentFailure(PaymentEvent paymentEvent) {
        for (PaymentEvent.PaymentItem item : paymentEvent.getItems()) {
            Inventory i =  inventoryRepository.findByProductId(item.getProductId()).orElse(null);
            if (i != null) {
                i.setReservedQuantity(i.getReservedQuantity() - item.getQuantity());
                i.setAvailableQuantity(i.getAvailableQuantity() + item.getQuantity());
                Movement movement = Movement.builder()
                        .delta(item.getQuantity())
                        .movementType(MovementType.RESERVE_CANCEL)
                        .userId(paymentEvent.getBuyerId())
                        .date(LocalDate.now())
                        .build();
                i.addMovement(movement);
                inventoryRepository.save(i);
            }
        }
    }

    public void initInventory(InventoryRequest request) {
        Inventory inventory = Inventory.builder()
                .availableQuantity(request.getStock())
                .reservedQuantity(0)
                .productId(request.getProductId())
                .movements(new ArrayList<>())
                .build();
        inventoryRepository.save(inventory);
    }

    public void initInventory(StockEvent stockEvent) {
        Inventory inventory = Inventory.builder()
                .availableQuantity(stockEvent.getStock())
                .reservedQuantity(0)
                .productId(stockEvent.getProductId())
                .movements(new ArrayList<>())
                .build();
        inventoryRepository.save(inventory);
    }
}
