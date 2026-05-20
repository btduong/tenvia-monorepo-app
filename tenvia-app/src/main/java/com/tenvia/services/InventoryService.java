package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.entities.UserEntity;
import com.tenvia.exception.UserIdNotFoundException;
import com.tenvia.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InventoryService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void useItem(Long userId, PowerUpType itemName) {
        UserEntity userEntity = getOrCreateInventory(userId);

        Map<PowerUpType, Integer> items = userEntity.getPowerUps();
        int currentAmount = items.getOrDefault(itemName, 0);

        if (currentAmount <= 0) {
            throw new IllegalStateException("You don't have any " + itemName + "s left!");
        }

        items.put(itemName, currentAmount - 1);
    }

    @Transactional
    public Map<PowerUpType, Integer> addItem(Long userId, PowerUpType type, int quantity) {
        UserEntity userEntity = getOrCreateInventory(userId);

        Map<PowerUpType, Integer> items = userEntity.getPowerUps();
        int currentCount = items.getOrDefault(type, 0);
        items.put(type, currentCount + quantity);
        return items;
    }

    private UserEntity getOrCreateInventory(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserIdNotFoundException(userId));
    }

}
