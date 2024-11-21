package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindEmailRequestDto {

    @NotNull(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[가-힣]{1,10}$", message = "이름은 한글 1자 이상 10자 이내로 입력해야 합니다.")
    private String name;

    @NotNull(message = "휴대폰번호를 입력해주세요.")
    @Pattern(regexp = "^010\\d{8}$", message = "휴대폰번호는 - 없이 010으로 시작하는 11자리 숫자로 입력해야 합니다.")
    private String phoneNumber;
}
