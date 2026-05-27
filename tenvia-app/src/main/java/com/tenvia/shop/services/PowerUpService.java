package com.tenvia.shop.services;

import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.services.GameSessionService;
import com.tenvia.shop.PowerUpType;
import com.tenvia.shop.dto.PowerUpResponseDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PowerUpService {

    private final GameSessionService gameSessionService;
    private final UserService userService;

    @Transactional
    public PowerUpResponseDTO applyPowerUp(Long userId, UUID sessionId, PowerUpType type) {
        // Verify the request to use the item is not from another user.
        gameSessionService.verifySessionIdOwner(sessionId, userId);
        UserDTO userDTO = userService.useItem(userId, type);

        AppliedEffectResult effectData = switch (type) {
            case FIFTY_FIFTY -> gameSessionService.applyFiftyFiftyOption(sessionId);
            case HAMMER ->  gameSessionService.applyHammerOption(sessionId);
            case SWAP_QUESTION -> gameSessionService.applySwapQuestionOption(sessionId);
        };

        return new PowerUpResponseDTO(userDTO, effectData);
    }
}
