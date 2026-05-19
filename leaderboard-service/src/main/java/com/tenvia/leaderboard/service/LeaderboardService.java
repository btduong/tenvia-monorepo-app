package com.tenvia.leaderboard.service;

import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.entity.LeaderboardScoreEntity;
import com.tenvia.leaderboard.repository.LeaderboardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Cacheable(value = "topScores")
    public List<LeaderboardDTO> getTopScores() {
        return leaderboardRepository.findTop10ByOrderByScoreDesc().stream()
                .map(LeaderboardDTO::from)
                .toList();
    }

    @Transactional
    public void saveScore(ScoreSubmittedEvent scoreSubmittedEvent) {
        log.info("Saving score for user: {}, score: {}", scoreSubmittedEvent.getUserName(), scoreSubmittedEvent.getScore());
        LeaderboardScoreEntity scoreEntity = new LeaderboardScoreEntity(scoreSubmittedEvent.getUserName(), scoreSubmittedEvent.getScore());
        leaderboardRepository.save(scoreEntity);
    }
}
