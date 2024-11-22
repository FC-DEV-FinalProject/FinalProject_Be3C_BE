package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberGetResponseDto {

    private Long id;

    @Schema(description = "역할코드", example = "RC001")
    private String roleCode;

    private String email;
    private String name;
    private String nickname;
    private LocalDate birth;
    private String phoneNumber;

}
