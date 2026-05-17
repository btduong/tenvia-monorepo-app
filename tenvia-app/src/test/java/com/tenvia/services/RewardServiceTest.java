package com.tenvia.services;

import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import org.h2.engine.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        UserEntity user = new UserEntity("alice");
        gameSession = new  GameSessionEntity(user, List.of(1L), 5);
        gameSession.updateCorrectAnswer();
        assertEquals(15, rewardService.calculateGold(gameSession));
    }
}