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

    private Long totalMemberCount;

    private Long userMemberCount;

    private Long TraderMemberCount;

    private Long ManagerMemberCount;
}
