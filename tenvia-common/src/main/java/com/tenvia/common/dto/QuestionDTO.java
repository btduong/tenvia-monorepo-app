package com.tenvia.common.dto;


import com.tenvia.common.types.QuestionTrait;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private Long id;
    private String questionText;
    private List<QuestionOptionDTO> options;
    private boolean powerUpDisabled;
    private String correctLetter;
    private String explanation;
    private Integer correctOptionId;
    private Integer expiresInSeconds;
    private QuestionTrait trait;
}
