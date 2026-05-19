package com.tenvia.dto;

public record GameSessionSummary(int score,
                                 int correctAnswerCount,
                                 int incorrectAnswerCount,
                                 int skipQuestionCount) {}
