package com.tenvia.dto;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;

import java.util.List;

public record QuestionResponse(
        Long id,
        String questionText,
        List<QuestionOptionDTO> options,
        boolean powerUpDisabled,
        Integer expiresInSecond,
        Integer index) {

    public static QuestionResponse from(QuestionDTO dto, int index, int expiresIn) {
        return new QuestionResponse(
                dto.getId(),
                dto.getQuestionText(),
                dto.getOptions(),
                dto.isPowerUpDisabled(),
                expiresIn,
                index
        );
    }
}
