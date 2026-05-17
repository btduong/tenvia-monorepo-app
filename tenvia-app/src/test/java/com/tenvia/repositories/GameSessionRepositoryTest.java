package com.tenvia.repositories;

import com.tenvia.TenviaApplication;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import org.h2.engine.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = TenviaApplication.class)
@DataJpaTest
class GameSessionRepositoryTest {

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findTop10ByIsOverTrueOrderByScoreDesc() {
        for (int i = 0; i < 10; i++) {
            UserEntity user = new UserEntity("Player " + i);
            testEntityManager.persist(user);
            GameSessionEntity session = new GameSessionEntity();
            session.setUser(user);
            session.setScore(i * 10);
            session.setOver(true); // Most are finished

            gameSessionRepository.save(session);
        }

        // A session not over yet
        GameSessionEntity sessionInPlay = new GameSessionEntity();
        UserEntity user = new UserEntity("Player.x");
        testEntityManager.persist(user);
        sessionInPlay.setUser(user);
        sessionInPlay.setScore(999);
        sessionInPlay.setOver(false);
        gameSessionRepository.save(sessionInPlay);

        List<GameSessionEntity> topScores = gameSessionRepository.findTop10ByIsOverTrueOrderByScoreDesc();

        assertEquals(10, topScores.size()); // Should return 10
        assertTrue(topScores.stream().noneMatch(p -> p.getScore() == 999));
    }

    @Test
    void oneUserCanHaveMultipleSessions() {
        UserEntity user = testEntityManager.persist(new UserEntity("Alice"));

        gameSessionRepository.save(GameSessionEntity.builder().user(user).isOver(true).score(50).build());
        gameSessionRepository.save(GameSessionEntity.builder().user(user).isOver(true).score(150).build());

        List<GameSessionEntity> results = gameSessionRepository.findTop10ByIsOverTrueOrderByScoreDesc();

        assertEquals(2, results.size());
        assertEquals("Alice", results.get(0).getUser().getUsername());
        assertEquals("Alice", results.get(1).getUser().getUsername());
    }
}