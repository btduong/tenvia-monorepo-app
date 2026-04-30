package com.tenvia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenvia.common.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {

    public static AnswerResponseDTO from(boolean isCorrect, QuestionDTO questionDTO, GameSessionSummary gameSessionSummary, int newBalance, boolean sessionIsOver) {
        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();
        answerResponseDTO.setCorrect(isCorrect);
        answerResponseDTO.setCorrectLetter(questionDTO.getCorrectLetter());
        answerResponseDTO.setExplanation(questionDTO.getExplanation());
        answerResponseDTO.setNewBalance(newBalance);
        answerResponseDTO.setGameOver(sessionIsOver);
        answerResponseDTO.setSummary(gameSessionSummary);
        return answerResponseDTO;
    }

    private boolean isCorrect;
    private String correctLetter;
    private String explanation;
    private int newBalance;
    private boolean isGameOver;
    private GameSessionSummary summary;

    @JsonProperty("isGameOver")
    public boolean isGameOver() {
        return isGameOver;
    }
}
