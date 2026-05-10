package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.UserDTO;
import com.tenvia.repositories.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PowerUpServiceTest {

    @Mock
    private InventoryService inventoryService;
    @Mock
    private GameSessionService gameSessionService;
    @Mock
    private UserService userService;
    @Mock
    private GameSessionRepository gameSessionRepository;
    @InjectMocks
    private PowerUpService powerUpService;

    @BeforeEach
    public void setUp() {

    }

    @Test
    void canApply_HammerPowerUp() {
        Long userId = 1L;
        UUID sessionId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO(1L, "Bob", LocalDateTime.now(), 0, new HashMap<>());
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult(List.of(666), true, PowerUpType.HAMMER);
        when(gameSessionService.applyHammerOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponse result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.HAMMER);
        assertEquals(result.updatedUser().username(), "Bob");
        assertEquals(result.updatedUser().id(), 1L);
        assertEquals(result.effectResult().appliedPowerUp(), PowerUpType.HAMMER);
        assertEquals(result.effectResult().canUsePowerUps(), true);
        assertEquals(result.effectResult().removeOptionIds(), List.of(666));
    }

    @Test
    void canApply_FiftyFiftyPowerUp() {
        Long userId = 1L;
        UUID sessionId = UUID.randomUUID();
        UserDTO userDTO = new UserDTO(1L, "Bob", LocalDateTime.now(), 0, new HashMap<>());
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult(List.of(10, 20), true, PowerUpType.FIFTY_FIFTY);
        when(gameSessionService.applyFiftyFiftyOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponse result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.FIFTY_FIFTY);
        assertEquals(result.updatedUser().username(), "Bob");
        assertEquals(result.updatedUser().id(), 1L);
        assertEquals(result.effectResult().appliedPowerUp(), PowerUpType.FIFTY_FIFTY);
        assertEquals(result.effectResult().canUsePowerUps(), true);
        assertEquals(result.effectResult().removeOptionIds(), List.of(10, 20));

    }
}