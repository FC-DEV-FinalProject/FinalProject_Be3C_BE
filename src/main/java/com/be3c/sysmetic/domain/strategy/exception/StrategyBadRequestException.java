package com.be3c.sysmetic.domain.strategy.exception;

// strategy bad request exception
public class StrategyBadRequestException extends RuntimeException {
    public StrategyBadRequestException(String message) {
        super(message);
    }

    public StrategyBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}