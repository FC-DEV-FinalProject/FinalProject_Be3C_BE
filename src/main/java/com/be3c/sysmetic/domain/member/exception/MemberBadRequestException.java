package com.be3c.sysmetic.domain.member.exception;

public class MemberBadRequestException extends RuntimeException {
    public MemberBadRequestException(String message) {
        super(message);
    }

    public MemberBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

}