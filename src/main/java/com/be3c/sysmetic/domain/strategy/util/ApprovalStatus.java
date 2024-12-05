package com.be3c.sysmetic.domain.strategy.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalStatus {
    REQUESTED("SA001", "승인요청"),
    APPROVED("SA002", "승인"),
    RETURNED("SA003", "반려"),
    NOT_REQUESTED("SA000", "요청 전"),
    DEFAULT("DEFAULT", "요청 전");

    private final String code;
    private final String description;

    public static String getDescriptionByCode(String code) {
        for (ApprovalStatus status : values()) {
            if (status.code.equals(code)) {
                return status.description;
            }
        }
        return DEFAULT.description;
    }
}