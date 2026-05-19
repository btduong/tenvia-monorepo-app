package com.tenvia.leaderboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "leaderboard_score")
@Getter
public class LeaderboardScoreEntity {

    @Id
    @GeneratedValue()
    private Long id;

    private String username;
    private Integer score;

    // required by JPA
    protected LeaderboardScoreEntity() {
    }

    public LeaderboardScoreEntity(String username, Integer score) {
        this.username = username;
        this.score = score;
    }
}
