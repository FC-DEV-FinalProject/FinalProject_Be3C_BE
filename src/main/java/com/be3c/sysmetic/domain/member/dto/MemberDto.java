package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    @NotNull
    // autoIncrease 적용 필요
    private Long id; // 회원 식별 번호

    @NotNull
    private String roleCode; // 회원 등급 코드

    @NotNull
    private String email; // 이메일

    @NotNull
    private String password; // 비밀번호

    @NotNull
    private String name; // 이름

    @NotNull
    private String nickname; // 닉네임

    @NotNull
    private LocalDateTime birth; // 생년월일

    @NotNull
    private String phoneNumber; // 휴대폰 번호

    @NotNull
    private String usingStatusCode; // 회원 상태 코드

    @NotNull
    private Integer totalFollow; // 총 팔로워 수

    @NotNull
    private String receiveInfoConsent; // 정보성 수신 동의 여부

    @NotNull
    private LocalDateTime infoConsentDate; // 정보성 수신 동의일

    @NotNull
    private String receiveMarketingConsent; // 마케팅 수신 동의 여부

    @NotNull
    private LocalDateTime marketingConsentDate; // 마케팅 수신 동의일
}

