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
        nickname : 트레이더 닉네임
        name : 전략명
        stocks : 종목
        smScore : SM Score
        // TODO 전일대비
    */
    private Long id;
    private Long traderId;
    private String nickname;
    private String name;
    private HashSet<StockListDto> stocks;
    private Double smScore;
    // TODO 전일대비
}
