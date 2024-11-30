package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MemberPutPasswordRequestDto: 회원 비밀번호 변경 요청 DTO")
public class MemberPutPasswordRequestDto {

    @Schema(description = "회원 ID", example = "12345")
    @NotNull(message = "userId는 필수 값입니다.")
    @JsonProperty("userId")
    private Long userId;

    @Schema(description = "현재 비밀번호", example = "current_password")
    @NotBlank(message = "currentPassword는 필수 값입니다.")
    @JsonProperty("currentPassword")
    private String currentPassword;

    @Schema(description = "새 비밀번호", example = "new_password123")
    @NotBlank(message = "newPassword는 필수 값입니다.")
    @JsonProperty("newPassword")
    private String newPassword;

    @Schema(description = "새 비밀번호 확인", example = "new_password123")
    @NotBlank(message = "newPasswordConfirm는 필수 값입니다.")
    @JsonProperty("newPasswordConfirm")
    private String newPasswordConfirm;
}
