package com.tenvia.mappers;

import com.tenvia.dto.QuestionDTO;
import com.tenvia.dto.QuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class QuestionResponseMapper {

    public QuestionResponse toQuestionResonse(QuestionDTO questionDTO) {
        return new QuestionResponse(questionDTO.getId(),
                questionDTO.getQuestionText(),
                questionDTO.getOptions(),
                questionDTO.isPowerUpDisabled());
    }
}
