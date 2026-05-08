package com.tenvia.services;

import com.tenvia.dto.UserDTO;
import com.tenvia.entities.InventoryEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.exception.UserIdNotFoundException;
import com.tenvia.repositories.InventoryRepository;
import com.tenvia.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    public UserEntity login(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    UserEntity user = UserEntity.builder()
                            .username(username)
                            .createdAt(LocalDateTime.now())
                            .balance(0)
                            .inventory(new HashMap<>())
                            .build();
                    return userRepository.save(user);
                });
    }

    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserIdNotFoundException(id));
    }

    /**
     * Update a user balance from the provided amount.
     *
     * @param userId
     * @param amount
     * @return
     */
    public int updateBalance(Long userId, int amount) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException(userId));

        // Calculate new balance
        int currentBalance = user.getBalance() != null ? user.getBalance() : 0;
        int newBalance = currentBalance + amount;

        // Can't spend more than the balance
        if (newBalance < 0) {
            throw new IllegalStateException("Insufficient gold coins!");
        }

        // Update user's balance
        user.setBalance(newBalance);
        userRepository.save(user);

        return newBalance;
    }

    public UserDTO getUserById(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow();

        // Fetch the inventory associated with this user
        InventoryEntity inventory = inventoryRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory missing"));

        // Build the DTO
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getCreatedAt(),
                user.getBalance(),
                inventory.getItems() // This is the Map<PowerUpType, Integer>
        );
    }
}
