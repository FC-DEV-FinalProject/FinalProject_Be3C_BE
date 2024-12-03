package com.be3c.sysmetic.global.util.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 메인 페이지 유저 비율 반환 dto")
public class MemberCountResponseDto {

    @Schema(description = "총 회원 수")
    private Long totalMemberCount;

    @Schema(description = "일반 사용자 회원 수")
    private Long userMemberCount;

    @Schema(description = "트레이더 회원 수")
    private Long TraderMemberCount;

    @Schema(description = "매니저 회원 수")
    private Long ManagerMemberCount;
}
