package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StrategyListDto {

    /*
        StrategyListDto : 전략 목록 응답 Dto

        strategyId : 전략 id
        traderId : 트레이더 id
        methodId : 매매방식 id
        traderNickname : 트레이더 닉네임
        name : 전략 명
        cycle : 주기
        String stock : 종목 (추후 List로 수정)
        // List<Stock> : 전략에 포함된 종목 리스트
        accumProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */

    private Long strategyId;
    private Long traderId;
    private Long methodId;
    private String traderNickname;
    private String name;
    private Character cycle;
    private String stock;
    private Double accumProfitLossRate;
    private Double mdd;
    private Double smScore;
}