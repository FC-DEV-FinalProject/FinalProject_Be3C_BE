package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraderStrategyListDto {
    private Long strategyId;
    private String strategyName;
    private String methodIconPath;
    private StockListDto stockList;
    private Character cycle;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}