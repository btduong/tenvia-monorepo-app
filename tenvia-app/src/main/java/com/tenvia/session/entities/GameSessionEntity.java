package com.tenvia.session.entities;

import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.shop.PowerUpType;
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
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Rich Domain Model represents a game session.
 */
@Entity
@Table(name = "game_session")
@Getter
public class GameSessionEntity {

    /**
     * Used by Spring Data for optimistic locking to prevent concurrent modification.
     */
    @Version
    private Long version;

    /**
     * Unique identifier for the game session.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    private UUID id;

    /**
     * Ordered list of question IDs for players to play.
     */
    @ElementCollection
    @CollectionTable(name = "session_question_ids", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "question_id")
    private List<Long> questionIds;

    /**
     * The zero-based index of the currently active question.
     */
    private int currentQuestionIndex = 0;

    /**
     * The total score accumulated by the user in this session.
     */
    private int score = 0;

    /**
     * Keeping track of how many questions are skipped due to timing out or missing answers.
     */
    private int skipQuestionCount = 0;

    /**
     * Indicating whether the game session has finished.
     */
    private boolean isOver = false;

    /**
     * The timestamp when the game session was started.
     */
    private LocalDateTime startTime;

    /**
     * The timestamp when the game session is scheduled to end.
     */
    private LocalDateTime endTime;

    /**
     * Total number of questions answered correctly.
     */
    private int correctAnswerCount = 0;

    /**
     * Total number of questions answered incorrectly.
     */
    private int incorrectAnswerCount = 0;

    /**
     * The timestamp when the current question was presented to the user.
     * Used to calculate timeouts.
     */
    private LocalDateTime questionStartTime;

    /**
     * The time limit in seconds before the answer is marked as expired.
     */
    private int questionTimeLimitInSeconds;

    /**
     * The Id of the user who owns and is playing this session.
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * The default maximum number of times power-up items can be used for a single question.
     * Some gameplay mechanics may allow this value to be increased or decreased.
     */
    @Column(name = "current_question_powerup_limit")
    private int powerUpLimit = 1;

    /**
     * A list of power-up items that have been activated for the current question.
     * This list size cannot exceed {@link #powerUpLimit}.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "session_active_powerups", joinColumns = @JoinColumn(name = "session_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "powerup_type")
    private List<PowerUpType> activePowerUps = new ArrayList<>();

    protected GameSessionEntity() {
    } // JPA compliant

    /**
     * Constructs a new game session with the given user, question ids, and time limits.
     *
     * @param userId                     the Id of the player owning the session
     * @param questionIds                the list of question IDs for this game
     * @param questionTimeLimitInSeconds the time allowed per question
     */
    public GameSessionEntity(Long userId, List<Long> questionIds, int questionTimeLimitInSeconds) {
        if (userId == null || questionIds == null || questionIds.isEmpty()) {
            throw new IllegalArgumentException("User and QuestionIds must be valid");
        }
        if (questionTimeLimitInSeconds <= 0) {
            throw new IllegalArgumentException("Time limit must be none zero");
        }

        this.userId = userId;
        this.questionIds = questionIds;
        this.questionTimeLimitInSeconds = questionTimeLimitInSeconds;
    }

    /**
     * Starts the session by a given session duration.
     * Timestamp the start time and computing the end time.
     *
     * @param sessionDurationInSecond the total maximum duration of the session
     */
    public void startSession(int sessionDurationInSecond) {
        startTime = LocalDateTime.now();
        endTime = startTime.plusSeconds(sessionDurationInSecond);
    }

    /**
     * Advances the session to the next question.
     * If there are no more questions, the session is marked as over.
     */
    public void advanceQuestionIndex() {
        currentQuestionIndex++;
        questionStartTime = null; // Reset the timestamp.
        if (currentQuestionIndex >= questionIds.size()) {
            isOver = true;
        }
    }

    /**
     * Checks if the user has reached the maximum allowed power-ups for the current question.
     *
     * @return true if power-up items have been used to the maximum limit.
     */
    public boolean hasReachedPowerUpLimit() {
        return activePowerUps.size() >= powerUpLimit;
    }

    /**
     * Records a power-up as being activated for the current question.
     *
     * @param powerUpType the type of power-up activated
     * @throws IllegalStateException if the power-up limit for this question has been reached
     */
    public void addActivatedPowerUp(PowerUpType powerUpType) {
        if (hasReachedPowerUpLimit()) {
            throw new IllegalStateException("Has reached max power-up item usage: " + powerUpLimit);
        }
        activePowerUps.add(powerUpType);
    }

    /**
     * Initializes the state for a new question, resetting the timer and power-up usage.
     */
    public void startNewQuestion() {
        questionStartTime = LocalDateTime.now();
        activePowerUps.clear();
        powerUpLimit = 1;
    }

    /**
     * Swaps the current question for a random question from the database.
     * This action immediately transitions the session into the new question state.
     *
     * @param newQuestionId the id of the new question
     * @throws GameSessionOverException if the session is already over
     */
    public void swapCurrentQuestion(Long newQuestionId) {
        if (isOver) {
            throw new GameSessionOverException(id);
        }

        questionIds.set(currentQuestionIndex, newQuestionId);
        startNewQuestion();
    }

    /**
     * Increments the count of skipped or timed-out questions.
     */
    public void advanceSkipCount() {
        skipQuestionCount++;
    }

    /**
     * Increments the score and correct answer count.
     */
    public void updateCorrectAnswer() {
        score++;
        correctAnswerCount++;
    }

    /**
     * Increments the incorrect answer count.
     */
    public void updateIncorrectAnswer() {
        incorrectAnswerCount++;
    }

    /**
     * Ends the game session.
     */
    public void endSession() {
        isOver = true;
    }

    /**
     * Retrieves the database ID of the question currently being presented to the user.
     *
     * @return the question ID
     */
    public Long getCurrentQuestionId() {
        return questionIds.get(currentQuestionIndex);
    }

    /**
     * Checks if the time limit for answering the current question has elapsed.
     *
     * @return true if the question has expired, false otherwise
     */
    public boolean isCurrentQuestionExpired() {
        // If this is the 1st question.
        if (questionStartTime == null) {
            return false;
        }

        return LocalDateTime.now().isAfter(questionStartTime.plusSeconds(questionTimeLimitInSeconds));
    }

    /**
     * Calculates the remaining time allowed to answer the current question.
     * Automatically initializes the question timer if it hasn't started yet.
     *
     * @return the remaining time in seconds (clamped to a minimum of 0)
     */
    public int getRemainingQuestionTimeInSeconds() {
        if (questionStartTime == null) {
            startNewQuestion();
            return questionTimeLimitInSeconds;
        } else {
            long elapsed = Duration.between(questionStartTime, LocalDateTime.now()).getSeconds();
            return (int) Math.max(questionTimeLimitInSeconds - elapsed, 0);
        }
    }

    /**
     * Checks a submitted answer against the correct answer.
     * Evaluates timeouts, updates session statistics (score, skip count, etc.),
     * and advances the session to the next question.
     *
     * @param selectedOptionId the option ID chosen by the user (can be null)
     * @param correctOptionId  the actual correct option ID for the question
     * @return true if the user selected the correct option within the time limit, false otherwise
     * @throws GameSessionOverException if attempting to answer when the session is over
     */
    public boolean checkAnswer(Long selectedOptionId, Long correctOptionId) {
        if (isOver) {
            throw new GameSessionOverException(id);
        }

        boolean hasTimedOut = isCurrentQuestionExpired();
        boolean skipped = hasTimedOut || selectedOptionId == null;
        boolean isCorrect = false;

        // If skipped then don't need to check for correct/incorrect answer.
        if (skipped) {
            advanceSkipCount();
        } else {
            isCorrect = correctOptionId.equals(selectedOptionId);
            if (isCorrect) {
                updateCorrectAnswer();
            } else {
                updateIncorrectAnswer();
            }
        }

        advanceQuestionIndex();

        return isCorrect;
    }

}