package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDto {

    @NotNull
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @JsonProperty("email")
    private String email; // 이메일

    @NotNull
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$", message = "비밀번호는 영문자(대, 소문자), 숫자, 특수문자를 포함하여 6~20자로 입력해야 합니다.")
    @JsonProperty("password")
    private String password; // 새로운 비밀번호

    @NotNull
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$", message = "비밀번호는 영문자(대, 소문자), 숫자, 특수문자를 포함하여 6~20자로 입력해야 합니다.")
    @JsonProperty("rewritePassword")
    private String rewritePassword; // 새로운 비밀번호 재입력

}
