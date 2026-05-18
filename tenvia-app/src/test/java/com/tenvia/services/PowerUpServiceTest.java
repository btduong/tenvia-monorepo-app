package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.PowerUpResponseDTO;
import com.tenvia.dto.QuestionResponse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private List<QuestionOptionDTO> options;

    @BeforeEach
    public void setUp() {
        userId = 1L;
        sessionId = UUID.randomUUID();
        userDTO = new UserDTO(1L, "Bob", LocalDateTime.now(), 0, new HashMap<>());
    }

    @Test
    void canApply_HammerPowerUp() {
        options = List.of(new QuestionOptionDTO(1L, "Option_1", "A", true),
                new QuestionOptionDTO(2L, "Option_2", "B", true),
                new QuestionOptionDTO(3L, "Option_3", "C", true),
                new QuestionOptionDTO(4L, "Option_4", "D", false));

        QuestionResponse questionResponse = new QuestionResponse(1L, "Q1", options, false, 15, 0);
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult(true, PowerUpType.HAMMER, questionResponse);
        when(gameSessionService.applyHammerOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponseDTO result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.HAMMER);
        assertEquals("Bob", result.updatedUser().username());
        assertEquals(1L, result.updatedUser().id());
        assertEquals(PowerUpType.HAMMER, result.effectResult().appliedPowerUp());
        assertTrue(result.effectResult().canUsePowerUps());
        assertFalse(result.effectResult().questionResponse().options().get(3).isAvailable());
    }

    @Test
    void canApply_FiftyFiftyPowerUp() {
        options = List.of(new QuestionOptionDTO(1L, "Option_1", "A", false),
                new QuestionOptionDTO(2L, "Option_2", "B", true),
                new QuestionOptionDTO(3L, "Option_3", "C", true),
                new QuestionOptionDTO(4L, "Option_4", "D", false));

        QuestionResponse questionResponse = new QuestionResponse(1L, "Q1", options, false, 15, 0);
        AppliedEffectResult appliedEffectResult = new AppliedEffectResult( true, PowerUpType.FIFTY_FIFTY, questionResponse);
        when(gameSessionService.applyFiftyFiftyOption(sessionId)).thenReturn(appliedEffectResult);
        when(userService.getUserById(userId)).thenReturn(userDTO);

        PowerUpResponseDTO result = powerUpService.applyPowerUp(userId, sessionId, PowerUpType.FIFTY_FIFTY);

        assertEquals("Bob", result.updatedUser().username());
        assertEquals(1L, result.updatedUser().id());
        assertEquals(PowerUpType.FIFTY_FIFTY, result.effectResult().appliedPowerUp());
        assertTrue(result.effectResult().canUsePowerUps());
        List<QuestionOptionDTO> unavailableQuestionOptions = result.effectResult().questionResponse().options().stream()
                .filter(opt -> !opt.isAvailable())
                .toList();
        assertEquals(2, unavailableQuestionOptions.size());
        assertThat(unavailableQuestionOptions.get(0).id()).isEqualTo(1L);
        assertThat(unavailableQuestionOptions.get(1).id()).isEqualTo(4L);

    }
}