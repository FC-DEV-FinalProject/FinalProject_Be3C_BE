package com.be3c.sysmetic.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailResponseDto {

    @NotNull
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @JsonProperty("email")
    private String email; // 이메일

    @JsonProperty("authCode")
    private String authCode; // 인증코드
}
