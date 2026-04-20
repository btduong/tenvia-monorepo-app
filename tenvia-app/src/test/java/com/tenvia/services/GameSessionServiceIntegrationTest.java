package com.tenvia.services;

import com.tenvia.TenviaApplication;
import com.tenvia.components.QuestionProvider;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import com.tenvia.repositories.GameSessionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Testcontainers
@ContextConfiguration(classes = TenviaApplication.class)
@SpringBootTest
@Transactional // roll back database after each test
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
    private QuestionProvider questionProvider;
    @MockitoBean
    private RewardService rewardService;
    @MockitoBean
    private UserService userService;

    private UUID activeSessionId;
    private UserEntity userEntity;
    private GameSessionEntity session;

    @BeforeEach
    void setUp() {

        userEntity = UserEntity.builder().username("username").id(1L).balance(0).build();


        session = new GameSessionEntity();
        session.setQuestionIds(List.of(1L, 2L));
        session.setCurrentQuestionIndex(0);
        session.setOver(false);
        session.setUser(userEntity);
        session.setGoldRewards(List.of(1,2,3,4,5,6));

        GameSessionEntity saved = gameSessionRepository.save(session);
        activeSessionId = saved.getId();
    }

    @Test
    @DisplayName("Question index increases regardless answer is correct or not")
    void validateAnswer_databasePersistData_expectSuccess () {
        when(userService.updateBalance(anyLong(), anyInt())).thenReturn(1);

        // question 1 - incorrect
        QuestionDTO questionDTO =  QuestionDTO.builder().correctOptionId(1).build();
        when(questionProvider.fetchQuestionById(anyLong())).thenReturn(questionDTO);
        gameSessionService.validateAnswer(activeSessionId, 100);
        GameSessionEntity updatedSession = gameSessionRepository.findById(activeSessionId).get();
        assertEquals(1, updatedSession.getCurrentQuestionIndex());
        assertEquals(0, updatedSession.getScore());

        // question 2 - correct
        when(rewardService.grantGold(session.getUser().getId(), 1)).thenReturn(1);
        QuestionDTO questionDTO2 =  QuestionDTO.builder().correctOptionId(400).build();
        when(questionProvider.fetchQuestionById(anyLong())).thenReturn(questionDTO2);
        gameSessionService.validateAnswer(activeSessionId, 400);
        updatedSession = gameSessionRepository.findById(activeSessionId).get();
        assertEquals(2, updatedSession.getCurrentQuestionIndex());
        assertEquals(1, updatedSession.getScore());
    }

    @Test
    void createNewSession_expectException_whenDatabaseIsEmpty() {
        when(questionProvider.fetchRandomQuestions(10)).thenThrow(new RuntimeException("Failed"));
        assertThrows(RuntimeException.class, () -> gameSessionService.createNewSession(1L, 10));
    }
}