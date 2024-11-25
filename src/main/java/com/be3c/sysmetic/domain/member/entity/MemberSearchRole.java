package com.be3c.sysmetic.domain.member.entity;

import lombok.Getter;

@Getter
public enum MemberSearchRole {
    ALL("ALL"),
    USER("USER"),
    TRADER("TRADER"),
    MANAGER("MANAGER");

    private final String code;

    MemberSearchRole(String code) {
        this.code = code;
    }
}
