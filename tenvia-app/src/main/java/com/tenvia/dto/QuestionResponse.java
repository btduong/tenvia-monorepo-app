package com.tenvia.dto;

import com.tenvia.common.dto.QuestionOptionDTO;

import java.util.List;

public record QuestionResponse(
        Long id,
        String questionText,
        List<QuestionOptionDTO> options,
        boolean powerUpDisabled,
        Integer expiresInSecond) {}
