package com.be3c.sysmetic.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberSearchType {
    EMAIL("EMAIL"),
    NAME("NAME"),
    NICKNAME("NICKNAME"),
    PHONENUMBER("NICKNAME");

    private final String code;

    MemberSearchType(String code) {
        this.code = code;
    }
}
