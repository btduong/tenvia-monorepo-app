package com.tenvia.session.exceptions;

import java.util.UUID;

public class InvalidSessionOwnerException extends RuntimeException {
    public InvalidSessionOwnerException(UUID sessionId, Long userId) {
        super("UserId " + userId + " does not own GameSessionId " + sessionId);
    }
}
