package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MainPageDto {

    /*
        MainPageDto : 메인 페이지 응답 Dto - 전략 하나에 대해서 정보 제공하면, 클릭했을 때 해당 전략으로 이동 가능

        rankedTrader : 트레이더 랭킹
        totalTraderCount : 총 트레이더 수
        totalStrategyCount : 총 전략 수(공개, 비공개 전부 포함)
        smScoreTopFives : SM Score Top 5개`
     */

    private List<TraderRankingDto> rankedTrader;
    private Long totalTraderCount;
    private Long totalStrategyCount;
    private List<SmScoreTopFive> smScoreTopFives;
}
