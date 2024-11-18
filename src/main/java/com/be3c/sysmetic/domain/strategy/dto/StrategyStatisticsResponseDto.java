package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StrategyStatisticsResponseDto {

    private Double currentBalance; // 잔고
    private Double accumulatedDepositWithdrawalAmount; // 누적입출금액
    private Double principal; // 원금
    private String operationPeriod; // 운용기간
    private LocalDate startDate; // 시작일자
    private LocalDate endDate; // 최종일자

    private Double accumulatedProfitLossAmount; // 누적손익금액
    private Double accumulatedProfitLossRate; // 누적손익률
    private Double maximumAccumulatedProfitLossAmount; // 최대누적손익금액
    private Double maximumAccumulatedProfitLossRate; // 최대누적손익률

    private Double currentCapitalReductionAmount; // 현재자본인하금액
    private Double currentCapitalReductionRate; // 현재자본인하율
    private Double maximumCapitalReductionAmount; // 최대자본인하금액
    private Double maximumCapitalReductionRate; // 최대자본인하율

    private Double averageProfitLossAmount; // 평균손익금액
    private Double averageProfitLossRate; // 평균손익률
    private Double maximumDailyProfitAmount; // 최대일이익금액
    private Double maximumDailyProfitRate; // 최대일이익률
    private Double maximumDailyLossAmount; // 최대일손실금액
    private Double maximumDailyLossRate; // 최대일손실률

    private Long totalTradingDays; // 총매매일수
    private Long totalProfitDays; // 총이익일수
    private Long totalLossDays; // 총손실일수
    private Long currentContinuousProfitLossDays; // 현재연속손익일수
    private Long maxContinuousProfitDays; // 최대연속이익일수
    private Long maxContinuousLossDays; // 최대연속손실일수

    private Double winningRate; // 승률
    private Double profitFactor;
    private Double roa;
    private Long highPointRenewalProgress; // 고점갱신후경과일

}
