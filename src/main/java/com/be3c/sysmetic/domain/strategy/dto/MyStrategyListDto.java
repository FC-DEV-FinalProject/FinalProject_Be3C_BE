package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MyStrategyListDto {
    private Long strategyId;
    private Long methodId;
    private String methodIconPath;
    private Character cycle;
    private StockListDto stockList;
    private String name;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}
