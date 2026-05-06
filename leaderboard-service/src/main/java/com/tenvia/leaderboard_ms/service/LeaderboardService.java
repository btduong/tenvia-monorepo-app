package com.tenvia.leaderboard_ms.service;

import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.leaderboard_ms.dto.LeaderboardDTO;
import com.tenvia.leaderboard_ms.entity.LeaderboardScoreEntity;
import com.tenvia.leaderboard_ms.repository.LeaderboardRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    private static final Logger LOG = LoggerFactory.getLogger(LeaderboardService.class);

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Cacheable(value = "topScores")
    public List<LeaderboardDTO> getTopScores() {
        return leaderboardRepository.findTop10ByOrderByScoreDesc().stream()
                .map(leaderboardScoreEntity -> LeaderboardDTO.builder()
                        .score(leaderboardScoreEntity.getScore())
                        .userName(leaderboardScoreEntity.getUsername())
                        .build())
                .toList();
    }

    @Transactional
    public void saveScore(ScoreSubmittedEvent scoreSubmittedEvent) {
        LOG.info("Saving score for user: {}, score: {}", scoreSubmittedEvent.getUserName(), scoreSubmittedEvent.getScore());
        LeaderboardScoreEntity scoreEntity = new LeaderboardScoreEntity();
        scoreEntity.setScore(scoreSubmittedEvent.getScore());
        scoreEntity.setUsername(scoreSubmittedEvent.getUserName());
        leaderboardRepository.save(scoreEntity);
    }
}
