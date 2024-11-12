package com.be3c.sysmetic.global.common;

public enum Code {
    USING_STATE("US001"),
    NOT_USING_STATE("US002"),

    //이력 관리 코드
    FOLLOW("FS001"),
    UNFOLLOW("FS002");

    String code;

    Code(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}