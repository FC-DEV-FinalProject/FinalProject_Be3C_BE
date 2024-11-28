package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StrategyDetailStatistics {
    /*
        StrategyDetailStatistics : 전략 상세 페이지에 필요한 통계 전달용 DTO

        maximumCapitalReductionAmount : 최대자본인하금액
        averageProfitLossRate : 평균손익률
        profitFactor : Profit Factor
        winningRate : 승률
    */

    private Double maximumCapitalReductionAmount;
    private Double averageProfitLossRate;
    private Double profitFactor;
    private Double winningRate;
}
