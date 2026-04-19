package com.tenvia.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionOptionDTO {
    /**
     * The unique id.
     */
    private Integer id;

    /**
     * The content of the option ie "The Vietnam war ended in 1975"
     */
    private String content;

    /**
     * The letter of the option ie "A"
     */
    private String letter;
}
