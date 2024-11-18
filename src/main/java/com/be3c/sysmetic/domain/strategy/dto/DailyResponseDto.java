package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponseDto {

    @Schema(description = "일간분석 식별번호", example = "123")
    @NotNull
    private Long dailyId; // 일간분석 데이터 식별번호

    @Schema(description = "일간분석 날짜", example = "2024-11-18")
    @NotNull
    private LocalDateTime date; // 날짜

    @Schema(description = "원금", example = "300000")
    @NotNull
    private Double principal; // 원금

    @NotNull
    private Double depositWithdrawalAmount; // 입출금

    @NotNull
    private Double profitLossAmount; // 일 손익

    @NotNull
    private Double profitLossRate; // 일 손익률

    private Double accumulatedProfitLossAmount; // 누적손익

    private Double accumulatedProfitLossRate; // 누적손익률
}
