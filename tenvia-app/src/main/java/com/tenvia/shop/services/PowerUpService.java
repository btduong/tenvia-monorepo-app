package com.tenvia.shop.services;

import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.services.GameSessionService;
import com.tenvia.shop.PowerUpType;
import com.tenvia.shop.dto.PowerUpResponseDTO;
import com.tenvia.shop.strategy.PowerUpStrategy;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PowerUpService {

    private final GameSessionService gameSessionService;
    private final UserService userService;
    private final Map<PowerUpType, PowerUpStrategy> strategyMap;

    public PowerUpService(GameSessionService gameSessionService, UserService userService, List<PowerUpStrategy> strategyList) {
        this.gameSessionService = gameSessionService;
        this.userService = userService;
        this.strategyMap = strategyList.stream().collect(Collectors.toMap(PowerUpStrategy::getPowerUpType, Function.identity()));
    }

    @Transactional
    public PowerUpResponseDTO applyPowerUp(Long userId, UUID sessionId, PowerUpType type) {
        // Verify the request to use the item is not from another user.
        gameSessionService.verifySessionIdOwner(sessionId, userId);

        UserDTO userDTO = userService.useItem(userId, type);

        AppliedEffectResult effectData = strategyMap.get(type).apply(sessionId);

        return new PowerUpResponseDTO(userDTO, effectData);
    }
}
