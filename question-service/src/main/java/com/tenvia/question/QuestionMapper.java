package com.tenvia.question;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.entities.QuestionOptionEntity;

import java.util.List;

/**
 * Utility class for mapping @link QuestionEntity to @link QuestionDTO.
 */
public final class QuestionMapper {

    public static QuestionDTO from(QuestionEntity questionEntity) {
        return QuestionDTO.builder()
                .questionText(questionEntity.getQuestionText())
                .id(questionEntity.getId())
                .options(questionEntity.getOptions().stream().map(QuestionMapper::toOptionDTO).toList())
                .correctLetter(questionEntity.getCorrectLetter())
                .explanation(questionEntity.getExplanation())
                .correctOptionId(findCorrectOptionId(questionEntity))
                .build();
    }

    public static List<QuestionDTO> from(List<QuestionEntity> questionEntityList) {
        return questionEntityList.stream()
                .map(QuestionMapper::from)
                .toList();
    }

    private static QuestionOptionDTO toOptionDTO(QuestionOptionEntity option) {
        QuestionOptionDTO questionOptionDTO = new QuestionOptionDTO();
        questionOptionDTO.setId(option.getId());
        questionOptionDTO.setContent(option.getContent());
        questionOptionDTO.setLetter(option.getLetter());
        return questionOptionDTO;
    }

    private static Integer findCorrectOptionId(QuestionEntity questionEntity) {
        return questionEntity.getOptions().stream()
                .filter(opt -> opt.getLetter().equalsIgnoreCase(questionEntity.getCorrectLetter()))
                .map(QuestionOptionEntity::getId)
                .findFirst()
                .orElse(null);
    }
}
