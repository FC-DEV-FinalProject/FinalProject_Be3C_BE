package com.be3c.sysmetic.domain.member.exception;

public class ResourceLimitExceededException extends RuntimeException{
    public ResourceLimitExceededException() {
        super();
    }

    public ResourceLimitExceededException(String message) {
        super(message);
    }

    public ResourceLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
