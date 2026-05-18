package com.tenvia.leaderboard.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leaderboard_score")
@Getter
@Setter
public class LeaderboardScoreEntity {

    @Id
    @GeneratedValue()
    private Long id;

    private String username;
    private Integer score;
}
