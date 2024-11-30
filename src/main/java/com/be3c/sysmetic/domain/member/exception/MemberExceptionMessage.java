package com.be3c.sysmetic.domain.member.exception;

public enum MemberExceptionMessage {
    // 회원가입 관련
    EMAIL_ALREADY_IN_USE("이미 사용 중인 이메일입니다. 다른 이메일을 입력해 주세요."),
    INVALID_EMAIL_CODE("인증코드가 일치하지 않습니다. 올바른 인증코드로 입력해 주세요."),
    REGISTRATION_FAILED("회원가입에 실패했습니다. 관리자에게 문의해 주세요."),
    NICKNAME_ALREADY_IN_USE("이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해 주세요."),

    // Redis 관련 오류
    REDIS_EMAIL_CODE_ERROR("Redis 이메일 인증코드 관련 오류 발생"),

    // 이메일 관련 오류
    EMAIL_ERROR("이메일 관련 오류 발생")
    ;


    private final String message;

    MemberExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}