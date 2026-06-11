package com.tenvia.shop.strategy;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.service.QuestionService;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.shop.PowerUpType;
import com.tenvia.user.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SwapStrategyTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private QuestionService questionService;
    @Mock
    private SessionConfig sessionConfig;
    @InjectMocks
    private SwapStrategy swapStrategy;

    private UUID sessionId;
    private QuestionDTO questionDTO;
    private GameSessionEntity session;

    @BeforeEach
    void setUp() {
        sessionId = UUID.randomUUID();
        when(sessionConfig.getQuestionTimeLimitInSeconds()).thenReturn(5);

        QuestionOptionDTO qOption1 = new QuestionOptionDTO(1L, "content-1", "A", true);
        QuestionOptionDTO qOption2 = new QuestionOptionDTO(2L, "content-2", "B", true);
        QuestionOptionDTO qOption3 = new QuestionOptionDTO(3L, "content-3", "C", true);
        QuestionOptionDTO qOption4 = new QuestionOptionDTO(4L, "content-4", "D", true);

        questionDTO = QuestionDTO.builder().correctOptionId(1L).options(List.of(qOption1, qOption2, qOption3, qOption4)).build();

        session = new GameSessionEntity(1L, Arrays.asList(1L), sessionConfig.getQuestionTimeLimitInSeconds());
        session.startSession(5);
    }

    @Test
    void canApply_expectException_whenSessionIsOver() {
        session.endSession();
        when(questionService.swapQuestion(anyList())).thenReturn(questionDTO);
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        assertThrows(GameSessionOverException.class, () -> swapStrategy.apply(sessionId));
    }

    @Test
    void canApply_getNewQuestion() {

        QuestionDTO swappedQuestion = QuestionDTO.builder().id(666L).build();
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(questionService.swapQuestion(anyList())).thenReturn(swappedQuestion);

        AppliedEffectResult result = swapStrategy.apply(sessionId);

        assertThat(result.questionResponse().id()).isEqualTo(666L);
    }

    @Test
    void canGetPowerUpType() {
        assertThat(swapStrategy.getPowerUpType()).isEqualTo(PowerUpType.SWAP_QUESTION);
    }
}