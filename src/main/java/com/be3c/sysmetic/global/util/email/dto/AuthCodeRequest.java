package com.be3c.sysmetic.global.util.email.dto;

/**
 * 인증 코드를 이메일로 발송할 때 사용
 * @param subscriber email주소
 * @param authCode 인증 코드
 */
public record AuthCodeRequest(
        String subscriber,
        String authCode
) implements EmailRequest {
}
