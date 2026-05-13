package com.tenvia.services;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.components.QuestionProvider;
import com.tenvia.config.SessionConfig;
import com.tenvia.dto.AnswerResponseDTO;
import com.tenvia.dto.GameSessionDTO;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.mappers.GameSessionMapper;
import com.tenvia.repositories.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GameSessionServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    @Mock
    private UserService userService;
    @Mock
    private RewardService rewardService;
    @Mock
    private QuestionProvider questionProvider;
    @Mock
    private GameSessionMapper gameSessionMapper;
    @Mock
    private ScoreProducer scoreProducer;
    @Mock
    private SessionConfig sessionConfig;

    @InjectMocks
    private GameSessionService gameSessionService;

    private GameSessionEntity session;
    private UUID sessionId;
    private UserEntity userEntity;
    private QuestionDTO questionDTO;

    @BeforeEach
    public void setUp() {
        sessionId = UUID.randomUUID();

        QuestionOptionDTO qOption1 = new QuestionOptionDTO();
        qOption1.setId(1);

        QuestionOptionDTO qOption2 = new QuestionOptionDTO();
        qOption2.setId(2);

        QuestionOptionDTO qOption3 = new QuestionOptionDTO();
        qOption3.setId(3);

        questionDTO = QuestionDTO.builder().correctOptionId(1).options(List.of(qOption1, qOption2, qOption3)).build();

        userEntity = UserEntity.builder().id(1L).username("username").balance(10).build();

        session = GameSessionEntity.builder()
                .id(sessionId)
                .questionIds(List.of(1L, 2L))
                .currentQuestionIndex(0)
                .isOver(false)
                .user(userEntity)
                .goldRewards(List.of(1,2,3))
                .activePowerUps(new ArrayList<>())
                .build();
    }

    @Test
    void createNewSession() {
        List<QuestionDTO> randomQuestions = List.of(questionDTO);
        GameSessionDTO gameSession = GameSessionDTO.builder().score(0).currentQuestionIndex(0).questions(randomQuestions).build();

        when(userService.findUserById(1L)).thenReturn(userEntity);
        when(gameSessionRepository.save(isA(GameSessionEntity.class))).thenReturn(session);
        when(questionProvider.fetchRandomQuestions(anyInt())).thenReturn(randomQuestions);
        when(gameSessionMapper.toDTO(any(), anyList())).thenReturn(gameSession);

        GameSessionDTO newSession = gameSessionService.createNewSession(1L, 1);

        assertEquals(0, newSession.getScore());
        assertEquals(0, newSession.getCurrentQuestionIndex());
        assertEquals(1, newSession.getQuestions().size());
        verify(gameSessionRepository).save(isA(GameSessionEntity.class));
    }

    @Test
    void createNewSession_expectException_whenNoQuestionAvailable() {
        when(questionProvider.fetchRandomQuestions(anyInt())).thenThrow(new RuntimeException("Failed"));
        assertThrows(RuntimeException.class, () -> gameSessionService.createNewSession(1L, 1));
    }

    @Test
    void validateAnswer_expectIncreaseIndex() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionProvider.fetchQuestionById(anyLong())).thenReturn(questionDTO);

        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1);

        assertEquals(1, session.getCurrentQuestionIndex());
        assertTrue(result.isCorrect());
    }

    @Test
    void validateAnswer_gameOver_expectException() {
        session.setOver(true);

        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));

        assertThrows(RuntimeException.class, () -> gameSessionService.validateAnswer(sessionId, 10));
    }

    @Test
    void validateAnswer_lastQuestionCorrect_expectGameOver() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionProvider.fetchQuestionById(anyLong())).thenReturn(questionDTO);


        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1);

        assertEquals(1, session.getCurrentQuestionIndex());
        assertTrue(result.isCorrect());

    }

    @Test
    void applyFiftyFiftyOption_expectTwoIds() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionProvider.fetchQuestionById(1L)).thenReturn(questionDTO);

        AppliedEffectResult result = gameSessionService.applyFiftyFiftyOption(sessionId);

        assertTrue(List.of(2,3).containsAll(result.removeOptionIds()));
    }

    @Test
    void applyHammerOption_expectOneId() {
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(questionProvider.fetchQuestionById(1L)).thenReturn(questionDTO);

        AppliedEffectResult result = gameSessionService.applyHammerOption(sessionId);
        assertEquals(List.of(2), result.removeOptionIds());
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
        session.setCurrentQuestionIndex(1);
        when(gameSessionRepository.findById(sessionId)).thenReturn(Optional.ofNullable(session));
        when(rewardService.calculateGold(session)).thenReturn(5);
        when(userService.updateBalance(any(), isA(Integer.class))).thenReturn(5);
        when(questionProvider.fetchQuestionById(anyLong())).thenReturn(questionDTO);

        AnswerResponseDTO result = gameSessionService.validateAnswer(sessionId, 1);
        assertTrue(session.isOver());
        assertTrue(result.isGameOver());
    }
}