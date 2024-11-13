package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 트레이더 별 전략 목록
public class StrategyListByTraderDto {
    /*
    methodIcon : 매매방식 아이콘
    periodIcon : 주기 아이콘
    stockIconList : 종목 아이콘 List
    traderName : 트레이더이름
    traderIcon : 트레이더아이콘
    mdd : 최대자본인하율
    smScore : SM Score
    accumulatedProfitRate : 누적 수익률
    followerCount : follower 수
     */

    private String traderNickname;
    private Long strategyId;
    private Long methodId;
    private Character cycle;
    // private List<String> stockIconList;     // 종목 다수 선택 & 아이콘 추후 고려
    private String strategyName;
    private Double mdd;
    private Double smScore;
    private Double accumProfitLossRate;
    private Long followerCount;
}