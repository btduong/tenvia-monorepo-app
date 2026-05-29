package com.tenvia.session.entities;

import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.user.entities.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameSessionEntityTest {

    private final Long correctOptionId = 100L;
    private final Long incorrectOptionId = 200L;
    private GameSessionEntity session;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity("alice");
        List<Long> questionIds = List.of(1L, 2L, 3L);
        int timeLimitInSeconds = 5;

        session = new GameSessionEntity(user, questionIds, timeLimitInSeconds);
        session.startSession(60);
        session.startNewQuestion();
    }

    @Test
    void checkAnswer_correctAnswer_updatesStatsAndAdvancesIndex() {
        boolean result = session.checkAnswer(correctOptionId, correctOptionId);

        assertThat(result).isTrue();
        assertThat(session.getScore()).isEqualTo(1);
        assertThat(session.getCorrectAnswerCount()).isEqualTo(1);
        assertThat(session.getIncorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getSkipQuestionCount()).isEqualTo(0);
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(1);
    }

    @Test
    void checkAnswer_incorrectAnswer_updatesStatsAndAdvancesIndex() {
        boolean result = session.checkAnswer(incorrectOptionId, correctOptionId);

        assertThat(result).isFalse();
        assertThat(session.getScore()).isEqualTo(0);
        assertThat(session.getCorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getIncorrectAnswerCount()).isEqualTo(1);
        assertThat(session.getSkipQuestionCount()).isEqualTo(0);
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(1);
    }

    @Test
    void checkAnswer_skipped_updatesSkipCountAndAdvancesIndex() {
        boolean result = session.checkAnswer(null, correctOptionId);

        assertThat(result).isFalse();
        assertThat(session.getScore()).isEqualTo(0);
        assertThat(session.getCorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getIncorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getSkipQuestionCount()).isEqualTo(1);
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(1);
    }

    @Test
    void checkAnswer_timedOut_treatedAsSkippedEvenIfAnswerIsCorrect() {
        // Simulate question timed out by setting startTime in the past
        ReflectionTestUtils.setField(session, "questionStartTime", LocalDateTime.now().minusSeconds(10));
        assertThat(session.isCurrentQuestionExpired()).isTrue();

        boolean result = session.checkAnswer(correctOptionId, correctOptionId);

        assertThat(result).isFalse();
        assertThat(session.getScore()).isEqualTo(0);
        assertThat(session.getCorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getIncorrectAnswerCount()).isEqualTo(0);
        assertThat(session.getSkipQuestionCount()).isEqualTo(1);
        assertThat(session.getCurrentQuestionIndex()).isEqualTo(1);
    }

    @Test
    void checkAnswer_throwsException_whenSessionIsOver() {
        session.endSession();

        assertThrows(GameSessionOverException.class, () ->
                session.checkAnswer(correctOptionId, correctOptionId)
        );
    }

    @Test
    void isCurrentQuestionExpired_returnsTrueWhenExpired() {
        ReflectionTestUtils.setField(session, "questionStartTime", LocalDateTime.now().minusSeconds(10));
        assertThat(session.isCurrentQuestionExpired()).isTrue();
    }

    @Test
    void isCurrentQuestionExpired_returnsFalseWhenNotExpired() {
        assertThat(session.isCurrentQuestionExpired()).isFalse();
    }
}
