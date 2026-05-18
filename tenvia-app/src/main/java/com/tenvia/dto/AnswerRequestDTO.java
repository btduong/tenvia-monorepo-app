package com.tenvia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerRequestDTO {

    @NotNull(message = "Question Id must not be null")
    private Long questionId;

    @NotNull(message = "Option Id must not be null")
    private Long selectedOptionId;
}
