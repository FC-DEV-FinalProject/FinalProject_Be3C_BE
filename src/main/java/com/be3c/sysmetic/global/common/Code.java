package com.be3c.sysmetic.global.common;

public enum Code {
    USING_STATE("US001"),
    NOT_USING_STATE("US002"),

    //이력 관리 코드
    FOLLOW("FS001"),
    UNFOLLOW("FS002"),

    // 비밀번호 변경 결과 코드
    PASSWORD_CHANGE_SUCCESS("PC001"),
    PASSWORD_CHANGE_FAIL("PC002");

    String code;

    Code(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}