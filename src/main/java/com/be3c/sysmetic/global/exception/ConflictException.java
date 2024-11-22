package com.be3c.sysmetic.global.exception;
public class ConflictException extends RuntimeException {
    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
