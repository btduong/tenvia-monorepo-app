package com.tenvia.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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
}
