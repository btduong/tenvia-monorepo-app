package com.tenvia.leaderboard_ms.service;

import com.tenvia.common.event.ScoreSubmittedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreConsumer {

    @Autowired
    private LeaderboardService leaderboardService;

    @RabbitListener(queues =  "scoring.queue")
    public void handleScoreSubmission(ScoreSubmittedEvent event) {
        leaderboardService.saveScore(event);
    }
}
