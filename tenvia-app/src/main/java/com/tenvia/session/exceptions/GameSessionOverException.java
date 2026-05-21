package com.tenvia.session.exceptions;

import java.util.UUID;

public class GameSessionOverException extends RuntimeException {
    public GameSessionOverException(UUID sessionId) {
        super("Session: '" + sessionId + "' is already finished");
    }
}
