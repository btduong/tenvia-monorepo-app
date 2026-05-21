package com.tenvia.session.services;

import com.tenvia.shop.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.session.components.ScoreProducer;
import com.tenvia.config.SessionConfig;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.dto.GameSessionDTO;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.question.service.QuestionService;
import com.tenvia.user.services.UserService;
import com.tenvia.session.repositories.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GameSessionServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private UserService userService;
    @Mock
    private ScoreProducer scoreProducer;
    @Mock
    private SessionConfig sessionConfig;
    @Mock
    private QuestionService questionService;

    @InjectMocks
    private GameSessionService gameSessionService;

    private GameSessionEntity session;
    private UUID sessionId;
    private UserEntity userEntity;
    private QuestionDTO questionDTO;

    @BeforeEach
    public void setUp() {
        sessionId = UUID.randomUUID();
        when(sessionConfig.getQuestionTimeLimitInSeconds()).thenReturn(5);

        QuestionOptionDTO qOption1 = new QuestionOptionDTO(1L, "content-1", "A", true);
        QuestionOptionDTO qOption2 = new QuestionOptionDTO(2L, "content-2", "B", true);
        QuestionOptionDTO qOption3 = new QuestionOptionDTO(3L, "content-3", "C", true);
        QuestionOptionDTO qOption4 = new QuestionOptionDTO(4L, "content-4", "D", true);

        questionDTO = QuestionDTO.builder().correctOptionId(1L).options(List.of(qOption1, qOption2, qOption3, qOption4)).build();

        userEntity = new UserEntity("username");

        session = new GameSessionEntity(userEntity, List.of(1L), sessionConfig.getQuestionTimeLimitInSeconds());
        session.startSession(5);
    }

    @Test
    void createNewSession() {
        List<QuestionDTO> randomQuestions = List.of(questionDTO);

        when(userService.findUserById(1L)).thenReturn(userEntity);
        when(questionService.fetchRandomQuestion(anyInt())).thenReturn(randomQuestions);
        when(gameSessionRepository.save(any())).thenReturn(session);

        GameSessionDTO newSession = gameSessionService.createNewSession(1L, 1);

        assertEquals(0, newSession.score());
        assertEquals(0, newSession.currentQuestionIndex());
        assertEquals(1, newSession.questions().size());
    }

    @Test
    void createNewSession_expectException_whenNoQuestionAvailable() {
        when(questionService.fetchRandomQuestion(anyInt())).thenThrow(new RuntimeException("Failed"));
        assertThrows(RuntimeException.class, () -> gameSessionService.createNewSession(1L, 1));
    }

    @Test
    void validateAnswer_expectIncreaseIndex() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);

        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1L);

        assertEquals(1, session.getCurrentQuestionIndex());
        assertTrue(result.isCorrect());
    }

    @Test
    void validateAnswer_gameOver_expectException() {
        session.endSession();

        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));

        assertThrows(RuntimeException.class, () -> gameSessionService.validateAnswer(sessionId, 10L));
    }

    @Test
    void validateAnswer_lastQuestionCorrect_expectGameOver() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);

        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1L);

        assertEquals(1, session.getCurrentQuestionIndex());
        assertTrue(result.isCorrect());

    }

    @Test
    void applyFiftyFiftyOption_expectTwoIds() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionService.getQuestionById(1L)).thenReturn(questionDTO);

        AppliedEffectResult result = gameSessionService.applyFiftyFiftyOption(sessionId);

        List<QuestionOptionDTO> unavailableOptions = result.questionResponse().options().stream().filter(opt -> !opt.isAvailable()).toList();
        assertEquals(2, unavailableOptions.size());
    }

    @Test
    void applyHammerOption_expectOneId() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionService.getQuestionById(1L)).thenReturn(questionDTO);

        AppliedEffectResult result = gameSessionService.applyHammerOption(sessionId);

        List<QuestionOptionDTO> unavailableOptions = result.questionResponse().options().stream().filter(opt -> !opt.isAvailable()).toList();
        assertEquals(1, unavailableOptions.size());
    }

    @Test
    void applyFiftyFiftyOption_expectException_whenMaxUsageReached() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));

        session.getActivePowerUps().add(PowerUpType.FIFTY_FIFTY);
        assertThrows(IllegalStateException.class, () -> gameSessionService.applyFiftyFiftyOption(sessionId));
    }

    @Test
    void applyHammer_expectException_whenMaxUsageReached() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));

        session.getActivePowerUps().add(PowerUpType.HAMMER);
        assertThrows(IllegalStateException.class, () -> gameSessionService.applyHammerOption(sessionId));
    }

    @Test
    void finishSession_getBaseResult_noCorrectQuestion() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);

        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1L);
        assertTrue(session.isOver());
        assertTrue(result.isGameOver());
    }
}