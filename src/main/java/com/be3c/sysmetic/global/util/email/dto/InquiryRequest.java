package com.be3c.sysmetic.global.util.email.dto;

/**
 * 문의 등록 알람 이메일 요청 양식
 * @param subscriber toEmail
 * @param inquirer 문의한 사람 닉네임
 * @param inquireId 문의 번호
 */
public record InquiryRequest(
        String subscriber,
        String inquirer,
        Long inquireId
) implements EmailRequest {
}
