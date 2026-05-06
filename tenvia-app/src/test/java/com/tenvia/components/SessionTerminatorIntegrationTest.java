package com.tenvia.components;

import com.tenvia.entities.GameSessionEntity;
import com.tenvia.repositories.GameSessionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class SessionTerminatorIntegrationTest {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private GameSessionMetrics gameSessionMetrics;
    @Autowired
    private SessionTerminator sessionTerminator;

    @Test
    public void expect_findAndTerminate_OneSession() {

        LocalDateTime now = LocalDateTime.now();
        GameSessionEntity session1 = GameSessionEntity.createInitial(null, null, null);
        session1.startSession(5);
        session1.setEndTime(now.minusHours(1));
        session1.setOver(false);

        gameSessionRepository.saveAllAndFlush(List.of(session1));

        sessionTerminator.findAndTerminate();

        GameSessionEntity updatedSession1 = gameSessionRepository.findById(session1.getId()).get();
        Assertions.assertTrue(updatedSession1.isOver());
    }

    @Test
    public void expect_findAndTerminate_ZeroSession() {
        LocalDateTime now = LocalDateTime.now();
        GameSessionEntity session1 = GameSessionEntity.createInitial(null, null, null);
        session1.startSession(60);
        session1.setOver(false);

        gameSessionRepository.saveAllAndFlush(List.of(session1));

        sessionTerminator.findAndTerminate();
        GameSessionEntity updatedSession1 = gameSessionRepository.findById(session1.getId()).get();
        Assertions.assertFalse(updatedSession1.isOver());
    }
}
