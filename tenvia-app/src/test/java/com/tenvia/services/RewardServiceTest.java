package com.tenvia.services;

import com.tenvia.entities.GameSessionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RewardServiceTest {

    private RewardService rewardService;
    private GameSessionEntity gameSession;

    @BeforeEach
    void setUp() {
        rewardService = new RewardService();
    }

    @Test
    void calculateGold() {
        gameSession = GameSessionEntity.builder().score(10).build();
        assertEquals(105, rewardService.calculateGold(gameSession));
    }
}