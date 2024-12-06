package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;

@Getter
@Builder
public class SmScoreTopFive {

    /*
        SmScoreTopFive : SM Score Top 5 응답 Dto

        id : 전략 id
        traderId : 트레이더 id
        traderProfileImage : 트레이더 프로필 이미지
        nickname : 트레이더 닉네임
        name : 전략명
        stocks : 종목
        smScore : SM Score
        accumulatedProfitLossRate : 누적 손익률
    */
    private Long id;
    private Long traderId;
    private String traderProfileImage;
    private String nickname;
    private String name;
    private StockListDto stocks;
    private Double smScore;
    private Double accumulatedProfitLossRate;
}
