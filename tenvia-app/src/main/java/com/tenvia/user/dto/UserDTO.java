package com.tenvia.user.dto;

import com.tenvia.shop.PowerUpType;
import com.tenvia.user.entities.UserEntity;

import java.time.LocalDateTime;
import java.util.Map;

public record UserDTO(Long id,
                      String username,
                      LocalDateTime createdAt,
                      int balance,
                      Map<PowerUpType, Integer> inventory// Map of Enum -> Quantity
) {

    public static UserDTO from(UserEntity userEntity) {
        return new UserDTO(userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getCreatedAt(),
                userEntity.getBalance(),
                userEntity.getInventory());
    }
}
