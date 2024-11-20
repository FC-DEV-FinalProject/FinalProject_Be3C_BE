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

    // 팔로우 메일 전송 여부 코드
    SEND_FOLLOW_MAIL("SM001"),
    NOT_SEND_FOLLOW_MAIL("SM002");

    private String code;
}
