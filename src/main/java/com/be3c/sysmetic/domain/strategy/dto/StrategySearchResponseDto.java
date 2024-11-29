package com.be3c.sysmetic.domain.strategy.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
@Builder
public class StrategySearchResponseDto {

    /*
        StrategySearchResponseDto : 전략 항목별 상세 검색 응답 DTO

        strategyId : 전략 id
        traderId : 트레이더 id
        methodId : 매매방식 id
        traderNickname : 트레이더 닉네임
        name : 전략 명
        cycle : 주기
        List<Stock> : 전략에 포함된 종목 리스트
        accumulatedProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */

    private Long strategyId;
    private Long traderId;
    private String traderNickname;
    private Long methodId;
    private String methodName;
    private String name;
    private Character cycle;
    private StockListDto stockList;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}
