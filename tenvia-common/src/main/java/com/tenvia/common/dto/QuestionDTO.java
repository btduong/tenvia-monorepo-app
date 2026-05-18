package com.tenvia.common.dto;


import lombok.Builder;

import java.util.List;

@Builder
public record QuestionDTO(Long id,
                          String questionText,
                          List<QuestionOptionDTO> options,
                          boolean powerUpDisabled,
                          String correctLetter,
                          String explanation,
                          Long correctOptionId,
                          Integer expiresInSeconds) {

}
