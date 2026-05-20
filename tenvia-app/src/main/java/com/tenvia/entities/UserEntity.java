package com.tenvia.entities;

import com.tenvia.PowerUpType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * The amount of currency a player currently owns.
     */
    private Integer balance = 0;

    /**
     * A map for storing a power type and the quantity.
     */
    @ElementCollection
    @CollectionTable(name = "user_powerups", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "power_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "quantity")
    private Map<PowerUpType, Integer> inventory = new HashMap<>();


    public UserEntity(String username) {
        this.username = username;
    }

    protected UserEntity() {}

    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * Add a power-up item by the quantity to the inventory.
     * @param type PowerUpType
     */
    public void addPowerUp(PowerUpType type, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be > 0. Received: " + quantity);
        }
        int current = inventory.getOrDefault(type, 0);
        inventory.put(type, current + quantity);
    }

    /**
     * Consume a power-up item.
     * @param type PowerUpType
     */
    public void consumePowerUp(PowerUpType type) {
        int current = inventory.getOrDefault(type, 0);
        if (current <= 0) {
            throw new IllegalStateException("You don't own any " + type);
        }
        inventory.put(type, current - 1);
    }

}
