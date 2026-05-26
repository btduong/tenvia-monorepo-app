package com.tenvia.question.exceptions;

public class QuestionNotFoundException extends RuntimeException {
    public QuestionNotFoundException(Long id) {
        super("Question with Id: " + id + " was not found");
    }
}
