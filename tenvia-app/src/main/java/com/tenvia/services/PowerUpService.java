package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.repositories.GameSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PowerUpService {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Transactional
    public PowerUpResponse applyPowerUp(Long userId, UUID sessionId, PowerUpType type) {
        inventoryService.consumeItem(userId, type);

        AppliedEffectResult effectData = switch (type) {
            case FIFTY_FIFTY -> gameSessionService.applyFiftyFiftyOption(sessionId);
            case HAMMER ->  gameSessionService.applyHammerOption(sessionId);
            default -> throw new IllegalArgumentException("Unknown PowerUp: " + type);
        };

        return new PowerUpResponse(userService.getUserById(userId), effectData);
    }
}
