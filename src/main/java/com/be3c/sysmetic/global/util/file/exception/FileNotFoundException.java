package com.be3c.sysmetic.global.util.file.exception;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String message) {
        super(message);
    }

    public FileNotFoundException() {
    }
}