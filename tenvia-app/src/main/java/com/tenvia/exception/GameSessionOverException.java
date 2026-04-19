package com.tenvia.exception;

import java.util.UUID;

public class GameSessionOverException extends RuntimeException {
    public GameSessionOverException(UUID sessionId) {
        super("Session: '" + sessionId + "' is already finished");
    }
}
