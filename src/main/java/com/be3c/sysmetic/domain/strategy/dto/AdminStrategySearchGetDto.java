package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 전략 검색 요청 DTO")
public class AdminStrategySearchGetDto {

    @Schema(description = "공개 상태", example = "OPEN")
    private String openStatus;

    @Schema(description = "승인 상태", example = "APPROVED")
    private String approvalStatus;

    @Schema(description = "검색 키워드", example = "전략 이름 또는 트레이더 이름")
    private String keyword;

    @Schema(description = "페이지 번호", example = "1")
    private Integer page;
}
