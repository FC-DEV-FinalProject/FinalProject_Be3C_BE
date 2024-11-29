package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
@Builder
public class MainPageAverageIndicator {

    /*
        MainPageAverageIndicator : 대표전략 평균 지표

        id : 전략 id
        stocks : 종목
        roaList : ROA 자산 수익률
        smScoreList: SM Score
    */
    private Long id;
    private HashSet<StockListDto> stocks;
    private List<Double> roaList;
    private List<Double> smScoreList;
    // TODO 기간 선택
}
