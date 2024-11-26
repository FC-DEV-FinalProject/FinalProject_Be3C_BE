package com.be3c.sysmetic.domain.member.dto;

import com.be3c.sysmetic.domain.member.entity.MemberRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @JsonProperty("profileImage")
    private MultipartFile profileImage; // 프로필 이미지 (선택)

    @NotNull
    @JsonProperty("roleCode")
    private MemberRole roleCode; // 회원 등급 코드

    @NotNull
    @JsonProperty("email")
    private String email; // 이메일

    @NotNull
    @JsonProperty("password")
    private String password; // 비밀번호

    @NotNull
    @JsonProperty("rewritePassword")
    private String rewritePassword; // 비밀번호 재입력

    @NotNull
    @JsonProperty("name")
    private String name; // 이름

    @NotNull
    @JsonProperty("nickname")
    private String nickname; // 닉네임

    @NotNull
    @JsonProperty("birth")
    private String birth; // 생년월일

    @NotNull
    @JsonProperty("phoneNumber")
    private String phoneNumber; // 휴대폰 번호

    @NotNull
    @JsonProperty("receiveInfoConsent")
    private Boolean receiveInfoConsent; // 정보성 수신 동의 여부

    @NotNull
    @JsonProperty("infoConsentDate")
    private String infoConsentDate; // 정보성 수신 동의일

    @NotNull
    @JsonProperty("receiveMarketingConsent")
    private Boolean receiveMarketingConsent; // 마케팅 수신 동의 여부

    @NotNull
    @JsonProperty("marketingConsentDate")
    private String marketingConsentDate; // 마케팅 수신 동의일
}

