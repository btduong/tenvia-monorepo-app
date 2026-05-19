package com.tenvia.leaderboard.service;

import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.entity.LeaderboardScoreEntity;
import com.tenvia.leaderboard.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;
    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void canGetTopScores() {
        List<LeaderboardScoreEntity> leaderboardScoreEntityList = List.of(
                new LeaderboardScoreEntity("alice", 10),
                new LeaderboardScoreEntity("bob", 20));
        when(leaderboardRepository.findTop10ByOrderByScoreDesc()).thenReturn(leaderboardScoreEntityList);

        List<LeaderboardDTO> topScores = leaderboardService.getTopScores();
        assertThat(topScores.size()).isEqualTo(2);
        LeaderboardDTO leaderboardDTO1 = topScores.get(0);
        assertThat(leaderboardDTO1.userName()).isEqualTo("alice");
        assertThat(leaderboardDTO1.score()).isEqualTo(10);
        LeaderboardDTO leaderboardDTO2 = topScores.get(1);
        assertThat(leaderboardDTO2.userName()).isEqualTo("bob");
        assertThat(leaderboardDTO2.score()).isEqualTo(20);

    }

    @Test
    void canSaveScore() {
        ScoreSubmittedEvent event = new ScoreSubmittedEvent("alice", 10);
        leaderboardService.saveScore(event);

        verify(leaderboardRepository).save(isA(LeaderboardScoreEntity.class));
    }
}