package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StrategyListDto {

    /*
        StrategyListDto : 전략 목록 응답 Dto

        strategyId : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 이미지
        methodId : 매매방식 id
        methodName : 매매방식명
        methodIconPath : 매매방식 아이콘 경로
        stockIconPath : 종목 아이콘 경로
        name : 전략 명
        cycle : 주기
        stockList : 종목 리스트
        accumulatedProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */
    private Long strategyId;
    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private Long methodId;
    private String methodName;
    private String methodIconPath;
    private List<String> stockIconPath;
    private String name;
    private Character cycle;
    private StockListDto stockList;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}