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

    public static ClientQuestionDTO from(QuestionDTO questionDTO, int index, int expiresIn) {
        return new ClientQuestionDTO(
                questionDTO.id(),
                questionDTO.questionText(),
                questionDTO.options(),
                questionDTO.powerUpDisabled(),
                expiresIn,
                index
        );
    }

    public static ClientQuestionDTO from(QuestionDTO questionDTO) {
        return new ClientQuestionDTO(
                questionDTO.id(),
                questionDTO.questionText(),
                questionDTO.options(),
                questionDTO.powerUpDisabled(),
                null,
                null
        );
    }
}
