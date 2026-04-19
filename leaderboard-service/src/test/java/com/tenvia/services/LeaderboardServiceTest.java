package com.tenvia.services;

import com.tenvia.leaderboard_ms.dto.LeaderboardDTO;
import com.tenvia.leaderboard_ms.entity.LeaderboardScoreEntity;
import com.tenvia.leaderboard_ms.repository.LeaderboardRepository;
import com.tenvia.leaderboard_ms.service.LeaderboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;
    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void getTopScores() {
        LeaderboardScoreEntity score1 = new LeaderboardScoreEntity();
        score1.setUsername("Alpha");
        score1.setScore(15);

        LeaderboardScoreEntity score2 = new LeaderboardScoreEntity();
        score2.setUsername("anonymous");
        score2.setScore(5);

        List<LeaderboardScoreEntity> leaderboardScoreEntityList = List.of(score1, score2);

        when(leaderboardRepository.findTop10ByOrderByScoreDesc()).thenReturn(leaderboardScoreEntityList);

        List<LeaderboardDTO> topScoresResult = leaderboardService.getTopScores();
        assertEquals(2, topScoresResult.size());
        assertEquals("Alpha", topScoresResult.get(0).getUserName());
        assertEquals("anonymous", topScoresResult.get(1).getUserName());
        assertEquals(15, topScoresResult.get(0).getScore());
        assertEquals(5, topScoresResult.get(1).getScore());

    }

}