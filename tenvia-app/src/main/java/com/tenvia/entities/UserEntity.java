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

    @ElementCollection
    @CollectionTable(name = "user_inventory", joinColumns = @JoinColumn(name = "user_id"))
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

}
