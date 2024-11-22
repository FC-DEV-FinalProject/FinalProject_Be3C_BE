package com.be3c.sysmetic.global.util.file.exception;

public class InvalidFileFormatException extends RuntimeException {
    public InvalidFileFormatException(String message) {
        super(message);
    }
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}