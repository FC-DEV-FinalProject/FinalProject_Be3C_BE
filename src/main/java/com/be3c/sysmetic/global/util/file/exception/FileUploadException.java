package com.be3c.sysmetic.global.util.file.exception;

public class FileUploadException extends RuntimeException {

    public FileUploadException(){}

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}