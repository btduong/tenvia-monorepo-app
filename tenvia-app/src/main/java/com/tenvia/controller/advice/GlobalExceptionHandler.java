package com.tenvia.controller.advice;

import com.tenvia.dto.ErrorResponseDTO;
import com.tenvia.exception.FiftyFiftyOptionUsedException;
import com.tenvia.exception.GameSessionOverException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
}
