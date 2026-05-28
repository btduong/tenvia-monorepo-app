package com.tenvia.session.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerRequestDTO {

    @NotNull(message = "Question Id must not be null")
    private Long questionId;

    private Long selectedOptionId;
}
