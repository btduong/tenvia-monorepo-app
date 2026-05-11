package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.PowerUpResponseDTO;
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

    private Long userId;
    private UUID sessionId;
    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        sessionId = UUID.randomUUID();
        userDTO = new UserDTO(1L, "Bob", LocalDateTime.now(), 0, new HashMap<>());
    }

    @Test
    void canApply_HammerPowerUp() {
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult(List.of(666), true, PowerUpType.HAMMER);
        when(gameSessionService.applyHammerOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponseDTO result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.HAMMER);
        assertEquals("Bob", result.updatedUser().username());
        assertEquals(1L, result.updatedUser().id());
        assertEquals(PowerUpType.HAMMER, result.effectResult().appliedPowerUp());
        assertTrue(result.effectResult().canUsePowerUps());
        assertEquals(result.effectResult().removeOptionIds(), List.of(666));
    }

    @Test
    void canApply_FiftyFiftyPowerUp() {
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult(List.of(10, 20), true, PowerUpType.FIFTY_FIFTY);
        when(gameSessionService.applyFiftyFiftyOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponseDTO result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.FIFTY_FIFTY);
        assertEquals("Bob", result.updatedUser().username());
        assertEquals(1L, result.updatedUser().id());
        assertEquals(PowerUpType.FIFTY_FIFTY, result.effectResult().appliedPowerUp());
        assertTrue(result.effectResult().canUsePowerUps());
        assertEquals(result.effectResult().removeOptionIds(), List.of(10, 20));

    }
}