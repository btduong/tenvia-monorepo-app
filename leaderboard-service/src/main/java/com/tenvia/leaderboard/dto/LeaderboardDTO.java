package com.tenvia.leaderboard.dto;

import com.tenvia.leaderboard.entity.LeaderboardScoreEntity;

public record LeaderboardDTO(String userName, Integer score) {

    public static LeaderboardDTO from(LeaderboardScoreEntity leaderboardScore) {
        return new LeaderboardDTO(leaderboardScore.getUsername(), leaderboardScore.getScore());
    }
}
