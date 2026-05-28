package com.tenvia.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenvia.common.dto.QuestionDTO;
import lombok.Builder;

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

    public static AnswerResponseDTO from(boolean isCorrect, QuestionDTO questionDTO, GameSessionSummary gameSessionSummary, int newBalance, boolean sessionIsOver, int currentQuestionIndex, boolean hasTimedOut) {
        return AnswerResponseDTO.builder()
                .isCorrect(isCorrect)
                .correctLetter(questionDTO.correctLetter())
                .explanation(questionDTO.explanation())
                .newBalance(newBalance)
                .isGameOver(sessionIsOver)
                .summary(gameSessionSummary)
                .currentQuestionIndex(currentQuestionIndex)
                .hasTimedOut(hasTimedOut)
                .build();
    }

}
