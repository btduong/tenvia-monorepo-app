package com.tenvia.leaderboard_ms.repository;

import com.tenvia.leaderboard_ms.entity.LeaderboardScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardScoreEntity, Long> {

    /**
     * Find top 10 highest score in ascending order.
     *
     * @return a List of  GameSessionEntity
     */
    List<LeaderboardScoreEntity> findTop10ByOrderByScoreDesc();
}
