package com.tenvia.session.components;

import com.tenvia.session.components.GameSessionMetrics;
import com.tenvia.session.components.SessionTerminator;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.user.entities.UserEntity;
import com.tenvia.session.repositories.GameSessionRepository;
import com.tenvia.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class SessionTerminatorIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private GameSessionMetrics gameSessionMetrics;
    @Autowired
    private SessionTerminator sessionTerminator;

    private static final List<Long> QUESTION_IDS = List.of(1L, 2L);

    private UserEntity user;

    @BeforeEach
    public void setUp() {
        user = new UserEntity("alice");
        userRepository.save(user);
    }

    @Test
    public void expect_findAndTerminate_OneSession() {

        int questionTimeLimitInSeconds = 5;
        GameSessionEntity session1 = new GameSessionEntity(user.getId(), QUESTION_IDS, questionTimeLimitInSeconds);
        session1.startSession(5);
        ReflectionTestUtils.setField(session1, "endTime", LocalDateTime.now().minusHours(1));

        gameSessionRepository.saveAllAndFlush(List.of(session1));

        sessionTerminator.findAndTerminate();

        GameSessionEntity updatedSession1 = gameSessionRepository.findById(session1.getId()).get();
        Assertions.assertTrue(updatedSession1.isOver());
    }

    @Test
    public void expect_findAndTerminate_ZeroSession() {
        int questionTimeLimitInSeconds = 5;
        GameSessionEntity session1 = new GameSessionEntity(user.getId(), QUESTION_IDS, questionTimeLimitInSeconds);
        session1.startSession(60);

        gameSessionRepository.saveAllAndFlush(List.of(session1));

        sessionTerminator.findAndTerminate();
        GameSessionEntity updatedSession1 = gameSessionRepository.findById(session1.getId()).get();
        Assertions.assertFalse(updatedSession1.isOver());
    }
}
