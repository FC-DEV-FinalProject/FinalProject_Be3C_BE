package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
            , message = "유효한 이메일 주소를 입력해야 합니다.")
    @JsonProperty("email")
    private String email; // 이메일

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$"
            , message = "비밀번호는 영문자(대,소문자), 숫자, 특수문자를 포함하여 6~20자로 입력해야 합니다.")
    @JsonProperty("password")
    private String password; // 비밀번호

    @NotNull
    @JsonProperty("rememberMe")
    private String rememberMe; // 로그인 유지 여부 //(Boolean)으로 바꿔야 한다.

}
