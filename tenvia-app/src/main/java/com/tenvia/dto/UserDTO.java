package com.tenvia.dto;

import com.tenvia.PowerUpType;

import java.time.LocalDateTime;
import java.util.Map;

public record UserDTO(Long id,
                      String username,
                      LocalDateTime createdAt,
                      int balance,
                      Map<PowerUpType, Integer> inventory// Map of Enum -> Quantity
) {}
