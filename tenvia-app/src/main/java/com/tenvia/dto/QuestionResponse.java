package com.tenvia.dto;

import java.util.List;

public record QuestionResponse(
        Long id,
        String questionText,
        List<QuestionOptionDTO> options,
        boolean powerUpDisabled) {}
