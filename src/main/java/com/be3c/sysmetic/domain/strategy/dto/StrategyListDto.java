package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StrategyListDto {

    private Long strategyId;
    private String name;
    private String stock;
    private Character cycle;
    private String traderNickname;
    private Double accumProfitRate;
    private Double mdd;
    private Double smScore;
}