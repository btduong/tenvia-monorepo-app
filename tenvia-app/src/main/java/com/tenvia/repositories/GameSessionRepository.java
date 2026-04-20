package com.tenvia.repositories;

import com.tenvia.entities.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {

    /**
     * Find top 10 highest score in ascending order.
     *
     * @return a List of  GameSessionEntity
     */
    List<GameSessionEntity> findTop10ByIsOverTrueOrderByScoreDesc();

    @Query("select g from game_session g where g.isCompleted = false and g.endTime < :now")
    List<GameSessionEntity> findExpiredSessions(LocalDateTime now);

    /**
     * A faster version of findExpiredSessions which manually calls g.isOver(true) in the code.
     * This query talks to the database directly and doesn't do g.isOver(true) as many as there are sessions to be set.
     * This needs to be in a transaction ie caller should be annotated with @Transactional
     *
     * @param now - current local date time
     * @return the number of rows were updated
     */
    @Modifying // Indicate Spring that this is an update or delete
    @Query("update GameSessionEntity g SET g.isOver = true where g.isOver = false AND g.endAt < :now")
    int findAndKillSessions(@Param("now") LocalDateTime now);
}
