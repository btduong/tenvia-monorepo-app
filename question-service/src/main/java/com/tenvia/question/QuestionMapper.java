package com.tenvia.question;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.entities.QuestionOptionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    public QuestionDTO toQuestionDTO(QuestionEntity questionEntity) {
        return QuestionDTO.builder()
                .questionText(questionEntity.getQuestionText())
                .id(questionEntity.getId())
                .options(questionEntity.getOptions().stream().map(opt -> {
                    QuestionOptionDTO questionOptionDTO = new QuestionOptionDTO();
                    questionOptionDTO.setId(opt.getId());
                    questionOptionDTO.setContent(opt.getContent());
                    questionOptionDTO.setLetter(opt.getLetter());
                    return questionOptionDTO;
                }).toList())
                .correctLetter(questionEntity.getCorrectLetter())
                .explanation(questionEntity.getExplanation())
                .correctOptionId(questionEntity.getOptions().stream().filter(opt -> opt.getLetter().equalsIgnoreCase(questionEntity.getCorrectLetter()))
                        .map(QuestionOptionEntity::getId)
                        .findFirst()
                        .orElse(null))
                .build();
    }

    public List<QuestionDTO> toQuestionDTO(List<QuestionEntity> questionEntityList) {
        return questionEntityList.stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList());
    }
}
