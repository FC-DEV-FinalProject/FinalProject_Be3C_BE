package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StrategyAlgorithmResponseDto {
    /*
        StrategyAlgorithmResposneDto : 전략 알고리즘 검색 응답 Dto

        algorithmType : 검색한 알고리즘 타입 이름
        id : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        methodId : 매매방식 id
        methodName : 매매방식명
        stockListDto : 종목 리스트
        name : 전략명
        cycle : 주기
        accumProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */
    private String algorithmType;
    private Long id;
    private Long traderId;
    private String traderNickname;
    private Long methodId;
    private String methodName;
    private StockListDto stockList;
    private String name;
    private Character cycle;
    private Double accumProfitLossRate;
    private Double mdd;
    private Double smScore;
}
