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
public class DailyGetResponseDto {

    @Schema(description = "일간분석 식별번호", example = "1")
    @NotNull
    private Long dailyId;

    @Schema(description = "일간분석 날짜", example = "2024-11-18")
    @NotNull
    private LocalDateTime date;

    @Schema(description = "원금", example = "288000000")
    @NotNull
    private Double principal;

    @Schema(description = "입출금", example = "0")
    @NotNull
    private Double depositWithdrawalAmount;

    @Schema(description = "일손익금", example = "-300000")
    @NotNull
    private Double profitLossAmount;

    @Schema(description = "일손익률", example = "-3.4%")
    @NotNull
    private Double profitLossRate;

    @Schema(description = "누적손익금", example = "64500000")
    @NotNull
    private Double accumulatedProfitLossAmount;

    @Schema(description = "누적손익률", example = "20.6322%")
    @NotNull
    private Double accumulatedProfitLossRate;

}
