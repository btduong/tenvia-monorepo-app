package com.tenvia.session.services;

import com.tenvia.TenviaApplication;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.question.dto.QuestionResponse;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.question.service.QuestionService;
import com.tenvia.user.services.UserService;
import com.tenvia.session.repositories.GameSessionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The test is using the MockMvc to send a fake http request for the session abandon test and it's simpler than using a WebTestClient. For several reasons:
 * - the test thread is one that do .save(session) but it does not commit it
 * - the HTTP request from WebTestClient is real but in a separate HTTP thread, going through the network stack; but it cannot see the session because of Transactional enforces isolation
 * The result is the test fail due to session not found.
 * By using MockMVc, it forces all the interaction on the test thread so everything is visible including the session.
 */
@AutoConfigureMockMvc
@Testcontainers
@ContextConfiguration(classes = TenviaApplication.class)
@SpringBootTest
@Transactional // roll back database after each test
@ActiveProfiles("test")
class GameSessionServiceIntegrationTest {

    @Container
    static RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void configProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBITMQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ_CONTAINER::getAmqpPort);
    }

    @Autowired
    private GameSessionService gameSessionService;
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private QuestionService questionService;

    @Autowired
    private SessionConfig sessionConfig;
    @Autowired
    private MockMvc mockMvc;

    private UUID activeSessionId;
    private UserEntity userEntity;
    private GameSessionEntity session;
    private static final List<Long> QUESTION_IDS = List.of(1L, 2L);
    @BeforeEach
    void setUp() {

        userEntity = new UserEntity("username");

        session = new GameSessionEntity(userEntity, QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());

        GameSessionEntity saved = gameSessionRepository.save(session);
        activeSessionId = saved.getId();
    }

    @Test
    @DisplayName("Question index increases regardless answer is correct or not")
    void validateAnswer_databasePersistData_expectSuccess() {

        // question 1 - incorrect
        QuestionDTO questionDTO = QuestionDTO.builder().correctOptionId(1L).build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);
        gameSessionService.validateAnswer(activeSessionId, 100L);
        GameSessionEntity updatedSession = gameSessionRepository.findById(activeSessionId).get();
        assertEquals(1, updatedSession.getCurrentQuestionIndex());
        assertEquals(0, updatedSession.getScore());

        // question 2 - correct
        QuestionDTO questionDTO2 = QuestionDTO.builder().correctOptionId(400L).build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO2);
        gameSessionService.validateAnswer(activeSessionId, 400L);
        updatedSession = gameSessionRepository.findById(activeSessionId).get();
        assertEquals(2, updatedSession.getCurrentQuestionIndex());
        assertEquals(1, updatedSession.getScore());
    }

    @Test
    void createNewSession_expectException_whenDatabaseIsEmpty() {
        when(questionService.fetchRandomQuestion(10)).thenThrow(new RuntimeException("Failed"));
        assertThrows(RuntimeException.class, () -> gameSessionService.createNewSession(1L, 10));
    }

    @Test
    void expectSkipValidation_whenQuestionTimedOut() {
        QuestionDTO questionDTO = QuestionDTO.builder().correctOptionId(1L).build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);
        QuestionResponse nextQuestion = gameSessionService.getNextQuestion(activeSessionId);
        assertEquals(0, nextQuestion.index());

        GameSessionEntity updatedSession = gameSessionRepository.findById(activeSessionId).get();

        assertEquals(1, nextQuestion.expiresInSecond());
        assertEquals(1, updatedSession.getQuestionTimeLimitInSeconds());

        int limitInSeconds = sessionConfig.getQuestionTimeLimitInSeconds();
        assertEquals(1, limitInSeconds);

        // Perhaps better to inject a clock into the service then it will all
        await().pollDelay(limitInSeconds + 1, TimeUnit.SECONDS)
                .atMost(3, TimeUnit.SECONDS)
                .until(() -> true);

        // Validate
        AnswerResponseDTO answerResponseDTO = gameSessionService.validateAnswer(activeSessionId, 100L);
        assertTrue(answerResponseDTO.hasTimedOut());
        assertEquals(1, session.getSkipQuestionCount());
    }

    @Test
    void expectAbandonSessionSuccessfully() throws Exception {
        mockMvc.perform(post("/sessions/{sessionId}/abandon", activeSessionId.toString()))
                .andExpect(status().isOk()); // Assert HTTP 200
        GameSessionEntity abandonedSession = gameSessionRepository.findById(session.getId()).get();
        assertTrue(abandonedSession.isOver());
    }
}