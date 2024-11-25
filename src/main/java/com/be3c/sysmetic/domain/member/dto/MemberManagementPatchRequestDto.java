package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MemberManagementPatchRequestDto: 회원등급 변경 DTO")
public class MemberManagementPatchRequestDto {

    @NotBlank
    private List<Long> memberId;

    @NotBlank
    private Boolean hasManagerRights;
}
