package com.be3c.sysmetic.domain.strategy.exception;

public enum StrategyExceptionMessage {
    INVALID_VALUE("유효하지 않은 값입니다.");

    private final String message;

    StrategyExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}