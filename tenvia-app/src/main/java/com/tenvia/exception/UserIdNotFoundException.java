package com.tenvia.exception;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}
