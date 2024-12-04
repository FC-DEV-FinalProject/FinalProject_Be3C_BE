package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StrategyListByNameDto {

    /*
        StrategyListByNameDto : 전략명으로 검색

        strategyId : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        methodIconPath : 매매 방식 아이콘 경로
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
    private String methodIconPath;
    private List<String> stockIconPath;
    private String strategyName;
    private Character cycle;
    private Long followerCount;
    private Integer strategyCount;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}
