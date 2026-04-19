package com.tenvia.repositories;

import com.tenvia.entities.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
