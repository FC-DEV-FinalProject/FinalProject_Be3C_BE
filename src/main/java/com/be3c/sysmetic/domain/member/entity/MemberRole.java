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


    public MemberRole promoteToManager() {
        if (this == USER) return USER_MANAGER;
        if (this == TRADER) return TRADER_MANAGER;
        return this;
    }

    public MemberRole demoteFromManager() {
        if (this == USER_MANAGER) return USER;
        if (this == TRADER_MANAGER) return TRADER;
        return this;
    }

    // roleCode가 "RC"로 시작하는 경우에만 실행
    public static MemberRole getEnumRole(String roleCode) {
        return switch (roleCode) {
            case "RC001" -> USER;
            case "RC002" -> TRADER;
            case "RC003" -> USER_MANAGER;
            case "RC004" -> TRADER_MANAGER;
            case "RC005" -> ADMIN;
            default -> throw new IllegalArgumentException("회원의 등급코드를 확인할 수 없습니다. 등급코드: " + roleCode);
        };
    }
}
