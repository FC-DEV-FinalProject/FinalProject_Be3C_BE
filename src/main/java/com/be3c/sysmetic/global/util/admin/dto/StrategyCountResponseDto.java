package com.be3c.sysmetic.global.util.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 메인 페이지 전략 수 반환 dto")
public class StrategyCountResponseDto {

    private Long totalStrategyCount;

    private Long openStrategyCount;

    private Long waitingStrategyCount;
}
