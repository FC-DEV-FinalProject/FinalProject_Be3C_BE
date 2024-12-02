package com.be3c.sysmetic.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberSearchType {
    ALL("ALL"),
    NICKNAME("NICKNAME"),
    EMAIL("EMAIL"),
    NAME("NAME"),
    PHONENUMBER("PHONENUMBER");

    private final String code;

    MemberSearchType(String code) {
        this.code = code;
    }
}
