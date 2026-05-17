package com.tenvia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Builder
public record AnswerResponseDTO(boolean isCorrect,
                                String correctLetter,
                                String explanation,
                                int newBalance,
                                @JsonProperty("isGameOver") boolean isGameOver,
                                GameSessionSummary summary,
                                boolean hasTimedOut,
                                int currentQuestionIndex) {


    public static AnswerResponseDTO createAnswerTimedOutResponse() {
        return AnswerResponseDTO.builder().hasTimedOut(true).build();
    }

    public static AnswerResponseDTO from(boolean isCorrect, QuestionDTO questionDTO, GameSessionSummary gameSessionSummary, int newBalance, boolean sessionIsOver, int currentQuestionIndex) {
        return AnswerResponseDTO.builder()
                .isCorrect(isCorrect)
                .correctLetter(questionDTO.getCorrectLetter())
                .explanation(questionDTO.getExplanation())
                .newBalance(newBalance)
                .isGameOver(sessionIsOver)
                .summary(gameSessionSummary)
                .currentQuestionIndex(currentQuestionIndex)
                .build();
    }

}
