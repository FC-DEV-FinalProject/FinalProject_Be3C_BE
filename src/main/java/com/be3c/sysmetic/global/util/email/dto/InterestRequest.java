package com.be3c.sysmetic.global.util.email.dto;

/**
 * 관심 등록 알람 이메일 요청 양식
 * @param subscriber toEmail
 */
public record InterestRequest(
        String subscriber
) implements EmailRequest {
}