package com.tenvia.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
public record QuestionDTO(Long id,
                          String questionText,
                          List<QuestionOptionDTO> options,
                          boolean powerUpDisabled,
                          String correctLetter,
                          String explanation,
                          Integer correctOptionId,
                          Integer expiresInSeconds) {

}
