package com.tenvia.question.dto;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;

import java.util.List;

public record ClientQuestionDTO(
        Long id,
        String questionText,
        List<QuestionOptionDTO> options,
        boolean powerUpDisabled,
        Integer expiresInSecond,
        Integer index) {

    public static ClientQuestionDTO from(QuestionDTO dto, int index, int expiresIn) {
        return new ClientQuestionDTO(
                dto.id(),
                dto.questionText(),
                dto.options(),
                dto.powerUpDisabled(),
                expiresIn,
                index
        );
    }
}
