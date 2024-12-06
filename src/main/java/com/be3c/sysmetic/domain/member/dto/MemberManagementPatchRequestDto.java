package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = "등급을 변경할 회원이 선택되지 않았습니다.")
    @Size(min =1, message = "최소 한 명 이상의 회원을 선택해 주세요.")
    private List<Long> memberId;

    @NotNull
    private Boolean hasManagerRights;
}
