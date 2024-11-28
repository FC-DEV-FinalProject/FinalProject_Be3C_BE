package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StrategyListByTraderDto {
    /*

        StrategyListByTraderDto : 트레이더별 전략 목록 응답용 DTO

        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        methodId : 매매방식 id
        methodName : 매매방식명
        stockList : 종목 리스트
        strategyId : 전략 ID
        strategyName : 전략명
        cycle : 주기
        followerCount : 팔로우 수
        strategyCount : 등록한 전략 개수
        accumulatedProfitLossRate : 누적손익률
        mdd : MDD
        smScore : SM Score
     */

    // 11월 24일
    private Long traderId;
    private String traderNickname;
    private Long methodId;
    private String methodName;
    private StockListDto stockList;
    private Long strategyId;
    private String strategyName;
    private Character cycle;
    private Long followerCount;
    private Long strategyCount;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}