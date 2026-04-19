package com.tenvia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {

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
