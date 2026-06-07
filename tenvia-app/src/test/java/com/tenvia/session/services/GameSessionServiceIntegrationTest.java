package com.tenvia.session.services;

import com.tenvia.TenviaApplication;
import com.tenvia.common.UserRole;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.config.SessionConfig;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import com.tenvia.common.security.JwtUtil;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.user.repositories.UserRepository;
import com.tenvia.user.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final List<Long> QUESTION_IDS = List.of(1L, 2L);

    @Container
    @ServiceConnection
    static RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GameSessionService gameSessionService;
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private UserRepository userRepository;
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
    private String token;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("username");
        userRepository.save(userEntity);

        session = new GameSessionEntity(userEntity, QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());
        GameSessionEntity saved = gameSessionRepository.save(session);
        activeSessionId = saved.getId();

        token = jwtUtil.generateToken(userEntity.getId(), userEntity.getRole());
    }

    @Test
    @DisplayName("Question index increases regardless answer is correct or not")
    void validateAnswer_databasePersistData_expectSuccess() {

        // question 1 - incorrect
        QuestionDTO questionDTO = QuestionDTO.builder().correctOptionId(1L).build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);
        gameSessionService.validateAnswer(activeSessionId, 100L, session.getUser().getId());
        GameSessionEntity updatedSession = gameSessionRepository.findById(activeSessionId).get();
        assertEquals(1, updatedSession.getCurrentQuestionIndex());
        assertEquals(0, updatedSession.getScore());

        // question 2 - correct
        QuestionDTO questionDTO2 = QuestionDTO.builder().correctOptionId(400L).build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO2);
        gameSessionService.validateAnswer(activeSessionId, 400L, session.getUser().getId());
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
        ClientQuestionDTO nextQuestion = gameSessionService.getNextQuestion(activeSessionId, session.getUser().getId());
        assertEquals(0, nextQuestion.index());

        GameSessionEntity updatedSession = gameSessionRepository.findById(activeSessionId).get();

        assertEquals(1, nextQuestion.expiresInSecond());
        assertEquals(1, updatedSession.getQuestionTimeLimitInSeconds());

        int limitInSeconds = sessionConfig.getQuestionTimeLimitInSeconds();
        assertEquals(1, limitInSeconds);

        // Perhaps better to inject a clock into the service then dont need to wait.
        await().pollDelay(limitInSeconds + 1, TimeUnit.SECONDS)
                .atMost(limitInSeconds + 2, TimeUnit.SECONDS)
                .until(() -> true);

        // Validate
        AnswerResponseDTO answerResponseDTO = gameSessionService.validateAnswer(activeSessionId, 100L, session.getUser().getId());
        assertTrue(answerResponseDTO.hasTimedOut());
        assertEquals(1, session.getSkipQuestionCount());
    }

    @Test
    void expectAbandonSessionSuccessfully() throws Exception {
        mockMvc.perform(post("/sessions/{sessionId}/abandon", activeSessionId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()); // Assert HTTP 200
        GameSessionEntity abandonedSession = gameSessionRepository.findById(session.getId()).get();
        assertTrue(abandonedSession.isOver());
    }

    @Test
    void expectNoNewQuestionStart_whenRepeatedlyGetNextQuestion() throws Exception {
        // Create a custom session for this test with 5s limit
        GameSessionEntity customSession = new GameSessionEntity(userEntity, QUESTION_IDS, 5);
        customSession.startSession(60);
        gameSessionRepository.saveAndFlush(customSession);

        UUID customSessionId = customSession.getId();

        QuestionDTO questionDTO = QuestionDTO.builder()
                .correctOptionId(1L)
                .questionText("who are you")
                .expiresInSeconds(15)
                .build();
        when(questionService.getQuestionById(anyLong())).thenReturn(questionDTO);

        String questionData1 = mockMvc.perform(get("/sessions/{sessionId}/questions/next", customSessionId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Wait for 1s, at most 2s before sending same request again.
        await().pollDelay(1, TimeUnit.SECONDS)
                .atMost(2, TimeUnit.SECONDS)
                .until(() -> true);

        String questionData2 = mockMvc.perform(get("/sessions/{sessionId}/questions/next", customSessionId.toString())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClientQuestionDTO clientQuestion2 = objectMapper.readValue(questionData2, ClientQuestionDTO.class);
        ClientQuestionDTO clientQuestion1 = objectMapper.readValue(questionData1, ClientQuestionDTO.class);
        assertThat(clientQuestion2.questionText()).isEqualTo("who are you");
        assertThat(clientQuestion2.questionText()).isEqualTo(clientQuestion1.questionText());
        assertThat(clientQuestion2.id()).isEqualTo(clientQuestion1.id());
        assertThat(clientQuestion2.expiresInSecond()).isLessThan(clientQuestion1.expiresInSecond());
        verify(questionService, times(2)).getQuestionById(anyLong());

    }
}