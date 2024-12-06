package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MainPageAnalysisDto {

    /*
        MainPageAnalysisDto : 메인 페이지 대표전략 분석지표 응답용 DTO

        smScoreTopStrategyName : SM Score 1위인 전략명
        xAxis : 날짜
        averageStandardAmount : 통합 기준가
        smScore : smScore 1위의 누적 손익률
    */

    private String smScoreTopStrategyName;
    private List<LocalDate> xAxisAverageStandardAmount;
    private List<Double> averageStandardAmount;
    private List<LocalDate> xAxisAccumulatedProfitLossRate;
    private List<Double> accumulatedProfitLossRate;
}
