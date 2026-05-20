package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.PowerUpResponseDTO;
import com.tenvia.dto.UserDTO;
import com.tenvia.repositories.GameSessionRepository;
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
        UserDTO userDTO = userService.useItem(userId, type);

        AppliedEffectResult effectData = switch (type) {
            case FIFTY_FIFTY -> gameSessionService.applyFiftyFiftyOption(sessionId);
            case HAMMER ->  gameSessionService.applyHammerOption(sessionId);
            case SWAP_QUESTION -> gameSessionService.applySwapQuestionOption(sessionId);
        };

        return new PowerUpResponseDTO(userService.getUserById(userId), effectData);
    }
}
