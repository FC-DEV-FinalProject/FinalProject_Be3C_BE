package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.entity.StrategyStatistics;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StrategyStatisticsGetResponseDto {

    @Schema(description = "잔고", example = "230000")
    @NotNull
    private Double currentBalance;

    @Schema(description = "누적입출금", example = "468000000")
    @NotNull
    private Double accumulatedDepositWithdrawalAmount;

    @Schema(description = "원금", example = "3400000")
    @NotNull
    private Double principal;

    @Schema(description = "운용기간", example = "2년 6개월")
    @NotBlank
    private String operationPeriod;

    @Schema(description = "시작일자", example = "2024-11-10")
    @NotNull
    private LocalDate startDate;

    @Schema(description = "최종일자", example = "2024-11-16")
    @NotNull
    private LocalDate endDate;

    @Schema(description = "누적손익금", example = "2460000")
    @NotNull
    private Double accumulatedProfitLossAmount;

    @Schema(description = "누적손익률", example = "7.88%")
    @NotNull
    private Double accumulatedProfitLossRate;

    @Schema(description = "최대누적손익금", example = "875000000")
    @NotNull
    private Double maximumAccumulatedProfitLossAmount;

    @Schema(description = "최대누적손익률", example = "21.223%")
    @NotNull
    private Double maximumAccumulatedProfitLossRate;

    @Schema(description = "현재 자본 인하 금액", example = "120000")
    @NotNull
    private Double currentCapitalReductionAmount;

    @Schema(description = "현재 자본 인하율", example = "2.5%")
    @NotNull
    private Double currentCapitalReductionRate;

    @Schema(description = "최대 자본 인하 금액", example = "340000")
    @NotNull
    private Double maximumCapitalReductionAmount;

    @Schema(description = "최대 자본 인하율", example = "5.7%")
    @NotNull
    private Double maximumCapitalReductionRate;

    @Schema(description = "평균 손익 금액", example = "3400")
    @NotNull
    private Double averageProfitLossAmount;

    @Schema(description = "평균 손익률", example = "0.56%")
    @NotNull
    private Double averageProfitLossRate;

    @Schema(description = "최대 일 이익 금액", example = "58000")
    @NotNull
    private Double maximumDailyProfitAmount;

    @Schema(description = "최대 일 이익률", example = "2.8%")
    @NotNull
    private Double maximumDailyProfitRate;

    @Schema(description = "최대 일 손실 금액", example = "-72000")
    @NotNull
    private Double maximumDailyLossAmount;

    @Schema(description = "최대 일 손실률", example = "-3.1%")
    @NotNull
    private Double maximumDailyLossRate;

    @Schema(description = "총 매매일수", example = "260")
    @NotNull
    private Long totalTradingDays;

    @Schema(description = "총 이익일수", example = "180")
    @NotNull
    private Long totalProfitDays;

    @Schema(description = "총 손실일수", example = "80")
    @NotNull
    private Long totalLossDays;

    @Schema(description = "현재 연속 손익일수", example = "12")
    @NotNull
    private Long currentContinuousProfitLossDays;

    @Schema(description = "최대 연속 이익일수", example = "20")
    @NotNull
    private Long maxContinuousProfitDays;

    @Schema(description = "최대 연속 손실일수", example = "8")
    @NotNull
    private Long maxContinuousLossDays;

    @Schema(description = "승률", example = "69.2%")
    @NotNull
    private Double winningRate;

    @Schema(description = "profit factor", example = "1.5")
    @NotNull
    private Double profitFactor;

    @Schema(description = "ROA(Return on Assets)", example = "12.8%")
    @NotNull
    private Double roa;

    @Schema(description = "고점 갱신 후 경과일", example = "25")
    @NotNull
    private Long highPointRenewalProgress;

    public static StrategyStatisticsGetResponseDto getResponseDto(StrategyStatistics statistics, String operationPeriod) {
        return StrategyStatisticsGetResponseDto.builder()
                .currentBalance(statistics.getCurrentBalance())
                .accumulatedDepositWithdrawalAmount(statistics.getAccumulatedDepositWithdrawalAmount())
                .principal(statistics.getPrincipal())
                .operationPeriod(operationPeriod)
                .startDate(statistics.getFirstRegistrationDate())
                .endDate(statistics.getLastRegistrationDate())
                .accumulatedProfitLossAmount(statistics.getAccumulatedProfitLossAmount())
                .accumulatedProfitLossRate(statistics.getAccumulatedProfitLossRate())
                .maximumAccumulatedProfitLossAmount(statistics.getMaximumAccumulatedProfitLossAmount())
                .maximumAccumulatedProfitLossRate(statistics.getMaximumAccumulatedProfitLossRate())
                .currentCapitalReductionAmount(statistics.getCurrentCapitalReductionAmount())
                .currentCapitalReductionRate(statistics.getCurrentCapitalReductionRate())
                .maximumCapitalReductionAmount(statistics.getMaximumCapitalReductionAmount())
                .maximumCapitalReductionRate(statistics.getMaximumCapitalReductionRate())
                .averageProfitLossAmount(statistics.getAverageProfitLossAmount())
                .averageProfitLossRate(statistics.getAverageProfitLossRate())
                .maximumDailyProfitAmount(statistics.getMaximumDailyProfitAmount())
                .maximumDailyProfitRate(statistics.getMaximumDailyProfitRate())
                .maximumDailyLossAmount(statistics.getMaximumDailyLossAmount())
                .maximumDailyLossRate(statistics.getMaximumDailyLossRate())
                .totalTradingDays(statistics.getTotalTradingDays())
                .totalProfitDays(statistics.getTotalProfitDays())
                .totalLossDays(statistics.getTotalLossDays())
                .currentContinuousProfitLossDays(statistics.getCurrentContinuousProfitLossDays())
                .maxContinuousProfitDays(statistics.getMaximumContinuousProfitDays())
                .maxContinuousLossDays(statistics.getMaximumContinuousLossDays())
                .winningRate(statistics.getWinningRate())
                .profitFactor(statistics.getProfitFactor())
                .roa(statistics.getRoa())
                .highPointRenewalProgress(statistics.getHighPointRenewalProgress())
                .build();
    }
}
