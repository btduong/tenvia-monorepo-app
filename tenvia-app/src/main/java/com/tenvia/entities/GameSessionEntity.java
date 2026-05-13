package com.tenvia.entities;

import com.tenvia.PowerUpType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game_session")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class GameSessionEntity {

    public static GameSessionEntity createInitial(UserEntity user, List<Long> questionIds, List<Integer> rewards) {
        return GameSessionEntity.builder()
                .user(user)
                .questionIds(questionIds)
                .goldRewards(rewards)
                .currentQuestionIndex(0)
                .build();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    private UUID id;

    @ElementCollection // all are records are stored in separate table
    @CollectionTable(name = "session_question_ids", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    private List<Long> questionIds;

    private int currentQuestionIndex = 0;

    private int score = 0;

    private boolean isOver = false;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isCompleted;
    private int correctAnswerCount = 0;
    private int incorrectAnswerCount = 0;
    private LocalDateTime questionStartTime;
    /**
     * The time limit in second before the answer is marked as expired.
     */
    private int questionTimeLimitInSeconds;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;


    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "session_rewards",
            joinColumns = @JoinColumn(name = "session_id") // Links to GameSession ID
    )
    @Column(name = "reward_amount") // The name of the value column
    private List<Integer> goldRewards = new ArrayList<>();

    /**
     * The default maximum number of times power-up items can be used for a single question.
     * Some gameplay mechanics may allow this value to be increased or decreased.
     */
    @Builder.Default
    @Column(name = "current_question_powerup_limit")
    private int powerUpLimit = 1;

    /***
     * A list of power-up items has been activated for the current question.
     * This list size cannot exceed @link powerUpLimit
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "session_active_powerups", joinColumns = @JoinColumn(name = "session_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "powerup_type")
    private List<PowerUpType> activePowerUps = new ArrayList<>();

    public void startSession(int sessionDurationInSecond) {
        startTime = LocalDateTime.now();
        endTime = startTime.plusSeconds(sessionDurationInSecond);
        isCompleted = false;
    }

    /***
     * Increase the current question index.
     */
    public void advanceQuestionIndex() {
        currentQuestionIndex++;
        questionStartTime = null; // Reset the timestamp.
        if (currentQuestionIndex >= questionIds.size()) {
            isOver = true;
        }
    }

    /***
     *
     * @return true if power-up items has been used to the maximum limit.
     */
    public boolean hasReachedPowerUpLimit() {
        return activePowerUps.size() >= powerUpLimit;
    }

    public void addActivatedPowerUp(PowerUpType powerUpType) {
        if (hasReachedPowerUpLimit()) {
            throw new IllegalStateException("Has reached max power-up item usage: " + powerUpLimit);
        }
        activePowerUps.add(powerUpType);
    }

    public void startNewQuestion() {
        questionStartTime = LocalDateTime.now();
        activePowerUps.clear();
        powerUpLimit = 1;
    }

}