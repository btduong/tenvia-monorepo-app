package com.tenvia.services;

import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.entity.LeaderboardScoreEntity;
import com.tenvia.leaderboard.repository.LeaderboardRepository;
import com.tenvia.leaderboard.service.LeaderboardService;
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
        LeaderboardScoreEntity score1 = new LeaderboardScoreEntity("alice", 15);

        LeaderboardScoreEntity score2 = new LeaderboardScoreEntity("bob", 5);

        List<LeaderboardScoreEntity> leaderboardScoreEntityList = List.of(score1, score2);

        when(leaderboardRepository.findTop10ByOrderByScoreDesc()).thenReturn(leaderboardScoreEntityList);

        List<LeaderboardDTO> topScoresResult = leaderboardService.getTopScores();
        assertEquals(2, topScoresResult.size());
        assertEquals("alice", topScoresResult.get(0).userName());
        assertEquals("bob", topScoresResult.get(1).userName());
        assertEquals(15, topScoresResult.get(0).score());
        assertEquals(5, topScoresResult.get(1).score());

    }

}