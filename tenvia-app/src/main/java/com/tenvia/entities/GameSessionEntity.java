package com.tenvia.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "game_session")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GameSessionEntity {

    public static GameSessionEntity createInitial(UserEntity user, List<Long> questionIds, List<Integer> rewards) {
        return GameSessionEntity.builder()
                .user(user)
                .questionIds(questionIds)
                .goldRewards(rewards)
                .currentQuestionIndex(0)
                .fiftyFiftyUsed(false)
                .build();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sessions_id")
    private UUID id;

//    @ManyToMany
//    @JoinTable(
//            name = "session_questions",
//            joinColumns = @JoinColumn(name = "session_id"), // foreign key of the owner
//            inverseJoinColumns = @JoinColumn(name = "question_id") //foreign key of the target
//    )
//    @OrderColumn(name = "question_order")// The 15 questions chosen for this specific game
//    private List<QuestionEntity> questions;

    @ElementCollection // all are records are stored in separate table
    @CollectionTable(name = "session_question_ids", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    private List<Long> questionIds;

    private boolean fiftyFiftyUsed = false;

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
    @Builder.Default
    private int questionTimeLimitInSeconds = 15;

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
        questionStartTime = null;
        if (currentQuestionIndex >= questionIds.size()) {
            isOver = true;
        }
    }

}