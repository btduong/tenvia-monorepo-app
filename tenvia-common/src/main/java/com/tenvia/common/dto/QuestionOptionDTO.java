package com.tenvia.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public record QuestionOptionDTO(Long id,
                                String content,
                                String letter,
                                @JsonProperty("isAvailable") Boolean isAvailable) {
}

