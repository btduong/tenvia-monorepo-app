package com.tenvia.session.repositories;

import com.tenvia.TenviaApplication;
import com.tenvia.config.SessionConfig;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.user.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = TenviaApplication.class)
@Import(SessionConfig.class)
@DataJpaTest
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionRepository gameSessionRepository;
    @Autowired
    private SessionConfig sessionConfig;

    @Autowired
    private TestEntityManager testEntityManager;

    private static final List<Long> QUESTION_IDS = List.of(1L, 2L);

    @Test
    void findTop10ByIsOverTrueOrderByScoreDesc() {
        for (int i = 0; i < 10; i++) {
            UserEntity user = new UserEntity("Player " + i);
            testEntityManager.persist(user);
            GameSessionEntity session = new GameSessionEntity(user.getId(), QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());
            ReflectionTestUtils.setField(session, "score", i * 10);
            session.endSession();
            gameSessionRepository.save(session);
        }

        // A session not over yet
        UserEntity user = new UserEntity("Player.x");
        user = testEntityManager.persistAndFlush(user);
        GameSessionEntity sessionInPlay = new GameSessionEntity(user.getId(), QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());
        ReflectionTestUtils.setField(sessionInPlay, "score", 999 );
        gameSessionRepository.save(sessionInPlay);

        List<GameSessionEntity> topScores = gameSessionRepository.findTop10ByIsOverTrueOrderByScoreDesc();

        assertEquals(10, topScores.size()); // Should return 10
        assertTrue(topScores.stream().noneMatch(p -> p.getScore() == 999));
    }

    @Test
    void oneUserCanHaveMultipleSessions() {
        UserEntity user = testEntityManager.persist(new UserEntity("Alice"));
        GameSessionEntity session1 = new GameSessionEntity(user.getId(), QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());
        GameSessionEntity session2 = new GameSessionEntity(user.getId(), QUESTION_IDS, sessionConfig.getQuestionTimeLimitInSeconds());
        ReflectionTestUtils.setField(session1, "score", 50);
        ReflectionTestUtils.setField(session2, "score", 100);
        session1.endSession();
        session2.endSession();
        gameSessionRepository.save(session1);
        gameSessionRepository.save(session2);

        List<GameSessionEntity> results = gameSessionRepository.findTop10ByIsOverTrueOrderByScoreDesc();

        assertEquals(2, results.size());
        assertEquals(user.getId(), results.get(0).getUserId());
        assertEquals(user.getId(), results.get(1).getUserId());
    }
}