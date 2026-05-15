package com.tenvia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.types.QuestionPenaltyTpe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {

    public static AnswerResponseDTO from(boolean isCorrect, QuestionDTO questionDTO, GameSessionSummary gameSessionSummary, int newBalance, boolean sessionIsOver, int currentQuestionIndex, PowerUpType grantedItem, Map<PowerUpType, Integer> updatedInventory, QuestionPenaltyTpe appliedPenalty) {
        AnswerResponseDTO answerResponseDTO = new AnswerResponseDTO();
        answerResponseDTO.setCorrect(isCorrect);
        answerResponseDTO.setCorrectLetter(questionDTO.getCorrectLetter());
        answerResponseDTO.setExplanation(questionDTO.getExplanation());
        answerResponseDTO.setNewBalance(newBalance);
        answerResponseDTO.setGameOver(sessionIsOver);
        answerResponseDTO.setSummary(gameSessionSummary);
        answerResponseDTO.setCurrentQuestionIndex(currentQuestionIndex);
        answerResponseDTO.setGrantedItem(grantedItem);
        answerResponseDTO.setUpdatedInventory(updatedInventory);
        answerResponseDTO.setAppliedPenalty(appliedPenalty);
        return answerResponseDTO;
    }

    private boolean isCorrect;
    private String correctLetter;
    private String explanation;
    private int newBalance;
    private boolean isGameOver;
    private GameSessionSummary summary;
    private boolean hasTimedOut;
    private int currentQuestionIndex;
    private PowerUpType grantedItem;
    private Map<PowerUpType, Integer> updatedInventory;
    private QuestionPenaltyTpe appliedPenalty;

    @JsonProperty("isGameOver")
    public boolean isGameOver() {
        return isGameOver;
    }
}
