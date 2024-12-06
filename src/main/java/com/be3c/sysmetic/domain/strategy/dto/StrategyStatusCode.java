package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StrategyStatusCode {
    PRIVATE("PRIVATE"),
    PUBLIC("PUBLIC"),
    REQUEST("REQUEST"),
    RETURN("RETURN"),
    NOT_USING_STATE("NOT_USING_STATE");

    String code;
}
