package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MemberPatchInfoRequestDto: 회원 정보 수정 요청 DTO")
public class MemberPatchInfoRequestDto {

    @Schema(description = "회원 ID", example = "12345")
    @NotNull(message = "userId는 필수 값입니다.")
    @JsonProperty("user_id")
    private Long userId;

    @Schema(description = "전화번호", example = "01012345678")
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Schema(description = "닉네임", example = "유저123")
    @JsonProperty("nickname")
    private String nickname;

    @Schema(description = "닉네임 중복 체크 여부", example = "true")
    @JsonProperty("nickname_dupl_check")
    private Boolean nicknameDuplCheck;

    @Schema(description = "정보 수신 동의 여부", example = "true")
    @JsonProperty("receive_info_consent")
    private Boolean receiveInfoConsent;

    @Schema(description = "마케팅 수신 동의 여부", example = "false")
    @JsonProperty("receive_marketing_consent")
    private Boolean receiveMarketingConsent;
}
