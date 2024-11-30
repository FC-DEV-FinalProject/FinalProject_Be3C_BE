package com.be3c.sysmetic.domain.strategy.exception;

import com.be3c.sysmetic.global.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

// strategy bad request exception
public class StrategyBadRequestException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public StrategyBadRequestException(String message, ErrorCode errorCode) {
        super(message);
        this.httpStatus = errorCode.getStatus();
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return httpStatus;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}