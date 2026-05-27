package com.tenvia.core.exception;

import com.tenvia.question.exceptions.QuestionNotFoundException;
import com.tenvia.session.exceptions.FiftyFiftyOptionUsedException;
import com.tenvia.session.exceptions.GameSessionOverException;
import com.tenvia.session.exceptions.InvalidSessionOwnerException;
import com.tenvia.user.exceptions.UserIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(QuestionNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleQuestionNotFound(QuestionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.builder()
                        .errorCode("QUESTION NOT FOUND")
                        .errorMessage(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(GameSessionOverException.class)
    public ResponseEntity<ErrorResponseDTO> handleSessionOver(GameSessionOverException ex) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body(ErrorResponseDTO.builder()
                        .errorCode("SESSION FINISHED")
                        .errorMessage(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(FiftyFiftyOptionUsedException.class)
    public ResponseEntity<ErrorResponseDTO> handleFiftyFiftyOptionUsed(FiftyFiftyOptionUsedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.builder()
                        .errorCode("50/50 ALREADY USED")
                        .errorMessage(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserIdNotFound(UserIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.builder()
                        .errorCode("userId does not exist")
                        .errorMessage(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidSessionOwnerException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidSessionOwner(InvalidSessionOwnerException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDTO.builder()
                        .errorCode("FORBIDDEN")
                        .errorMessage(ex.getMessage())
                        .build());
    }
}
