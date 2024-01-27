package com.girok.girokserver.core.exception;

public class CustomInternalException extends RuntimeException {

    public CustomInternalException(String message) {
        super(message);
    }

    public CustomInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
