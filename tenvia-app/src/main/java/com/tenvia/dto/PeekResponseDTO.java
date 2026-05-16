package com.tenvia.dto;

import com.tenvia.PowerUpType;
import com.tenvia.common.types.QuestionPenaltyTpe;
import com.tenvia.common.types.QuestionTrait;

public record PeekResponseDTO(
        String questionText,
        PowerUpType potentialReward,
        QuestionPenaltyTpe potentialPenalty,
        QuestionTrait trait,
        int timeLimit,
        int questionIndex
        ) {
}
