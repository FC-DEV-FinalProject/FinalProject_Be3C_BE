package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 등록 페이지 조회 응답 DTO")
public class InquirySavePageShowResponseDto {

    // 전략 위 아이콘

    @Schema(description = "전략명", example = "투자 전략 X")
    private String strategyName;

    // 트레이더 아이콘

    @Schema(description = "트레이더 닉네임", example = "TraderX")
    private String traderNickname;
}
