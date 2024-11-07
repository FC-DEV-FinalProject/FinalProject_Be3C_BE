package com.be3c.sysmetic.domain.strategy.exception;

public enum StrategyExceptionMessage {
    INVALID_VALUE("유효하지 않은 값입니다."),
    INVALID_PARAMETER("유효하지 않은 parameter입니다."),
    DATA_NOT_FOUND("존재하지 않는 data입니다."),
    DUPLICATE_STRATEGY_NAME("중복된 전략명입니다.");

    private final String message;

    StrategyExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}