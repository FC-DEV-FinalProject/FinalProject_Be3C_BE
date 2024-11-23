package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "MemberManagementPatchResponseDto: 회원등급 변경 DTO")
public class MemberManagementPatchResponseDto {

    private List<Long> memberId;

    private Boolean hasManagerRights;
}
