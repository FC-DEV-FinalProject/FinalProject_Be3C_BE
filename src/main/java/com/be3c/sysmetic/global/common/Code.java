package com.be3c.sysmetic.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum Code {
    USING_STATE("US001"),
    NOT_USING_STATE("US002"),

    //이력 관리 코드
    FOLLOW("FS001"),
    UNFOLLOW("FS002"),

    //전략 승인 관리 코드
    APPROVE_WAIT("SA001"),
    APPROVE_SUCCESS("SA002"),
    APPROVE_REJECT("SA003"),

    // 전략 상태 코드
    OPEN_STRATEGY("ST001"),
    CLOSE_STRATEGY("ST002"),
    WAIT_STRATEGY("ST003");

    String code;

    }
