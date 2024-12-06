package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "{NotEmpty.email}")
    @Email(message = "{Invalid.email}")
    @JsonProperty("email")
    private String email; // 이메일

    @NotBlank(message = "{NotEmpty.password}")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{6,20}$"
            , message = "{Invalid.password}")
    @JsonProperty("password")
    private String password; // 비밀번호

    @NotNull
    @JsonProperty("rememberMe")
    private Boolean rememberMe; // 로그인 유지 여부

}
