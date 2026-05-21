package com.tenvia.session.dto;

public record GameSessionSummary(int score,
                                 int correctAnswerCount,
                                 int incorrectAnswerCount,
                                 int skipQuestionCount) {}
