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

    @NotNull(message = "{NotEmpty.name}")
    @Pattern(regexp = "^[가-힣]{1,10}$", message = "{Invalid.name}")
    private String name;

    @NotNull(message = "{NotEmpty.phoneNumber}")
    @Pattern(regexp = "^010\\d{8}$", message = "{Invalid.phoneNumber}")
    private String phoneNumber;
}
