package com.tenvia.session.exceptions;

public class FiftyFiftyOptionUsedException extends RuntimeException {
    public FiftyFiftyOptionUsedException(String message) {
        super(message);
    }

    public FiftyFiftyOptionUsedException() {
      super("Option 50/50 has already been spent");
    }
}
