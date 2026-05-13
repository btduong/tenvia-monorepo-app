package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.PowerUpResponseDTO;
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
    public PowerUpResponseDTO applyPowerUp(Long userId, UUID sessionId, PowerUpType type) {
        inventoryService.consumeItem(userId, type);

        AppliedEffectResult effectData = switch (type) {
            case FIFTY_FIFTY -> gameSessionService.applyFiftyFiftyOption(sessionId);
            case HAMMER ->  gameSessionService.applyHammerOption(sessionId);
            case SWAP_QUESTION -> gameSessionService.applySwapQuestionOption(sessionId);
            default -> throw new IllegalArgumentException("Unknown PowerUp: " + type);
        };

        return new PowerUpResponseDTO(userService.getUserById(userId), effectData);
    }
}
