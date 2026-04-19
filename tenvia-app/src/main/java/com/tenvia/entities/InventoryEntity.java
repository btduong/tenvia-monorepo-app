package com.tenvia.entities;

import com.tenvia.PowerUpType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private UserEntity user;

    @ElementCollection
    @CollectionTable(name = "user_items", joinColumns = @JoinColumn(name = "inventory_id"))
    @MapKeyColumn(name = "item_name")
    @Column(name = "quantity")
    private Map<PowerUpType, Integer> items = new HashMap<>(); // Example: {"FIFTY_FIFTY": 3, "HAMMER": 1}
}