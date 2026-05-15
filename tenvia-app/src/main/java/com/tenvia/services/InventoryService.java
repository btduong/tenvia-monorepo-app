package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.entities.InventoryEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.repositories.InventoryRepository;
import com.tenvia.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public InventoryEntity getOrCreateInventory(Long userId) {
        return inventoryRepo.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findById(userId).orElseThrow();
                    InventoryEntity newInventory = new InventoryEntity();
                    newInventory.setUser(user);
                    return inventoryRepo.save(newInventory);
                });
    }

    @Transactional
    public void useItem(Long userId, PowerUpType itemName) {
        InventoryEntity inventory = getOrCreateInventory(userId);

        int currentAmount = inventory.getItems().getOrDefault(itemName, 0);

        if (currentAmount <= 0) {
            throw new IllegalStateException("You don't have any " + itemName + "s left!");
        }

        // Subtract one item
        inventory.getItems().put(itemName, currentAmount - 1);
        // Dirty checking saves this automatically
    }

    @Transactional
    public Map<PowerUpType, Integer> addItem(Long userId, PowerUpType type, int quantity) {
        InventoryEntity inventory = getOrCreateInventory(userId);

        Map<PowerUpType, Integer> items = inventory.getItems();
        int currentCount = items.getOrDefault(type, 0);

        items.put(type, currentCount + quantity);
        return items;
    }

}
