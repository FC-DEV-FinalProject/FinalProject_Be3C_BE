package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 전략 조회 응답 DTO")
public class AdminStrategyGetResponseDto {

    @Schema(description = "전략 ID", example = "1001")
    private Long strategyId;

    @Schema(description = "전략 이름", example = "최적의 투자 전략")
    private String strategyName;

    @Schema(description = "트레이더 이름", example = "John Doe")
    private String traderName;

    @Schema(description = "공개 상태 코드", example = "OPEN")
    private String openStatusCode;

    @Schema(description = "승인 상태 코드", example = "APPROVED")
    private String ApprovalStatusCode;

    @Schema(description = "전략 생성 날짜", example = "2024-11-20T14:23:45")
    private LocalDateTime strategyCreateDate;

    @Schema(description = "매매유형 아이디")
    private Long methodId;

    @Schema(description = "매매유형 아이콘 경로")
    private String methodIconPath;

    @Schema(description = "종목 아이콘 경로")
    private StockListDto stockList;
}
