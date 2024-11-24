package com.be3c.sysmetic.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberRole {
    USER("RC001"),
    TRADER("RC002"),
    USER_MANAGER("RC003"),
    TRADER_MANAGER("RC004"),
    ADMIN("RC005");

    private final String code;

    MemberRole(String code) {
        this.code = code;
    }
}
