package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class StrategyAnalysisResponseDto {

    /*
        StrategyDetailGraph - 전략 상세 분석 지표

        xAxis : x축 데이터 - 날짜
        yAxis : y축 데이터
            "yAxisData": {
                "DAILY_PROFIT_LOSS_AMOUNT": [1000.0, 1500.0, 2000.0],
                "CURRENT_BALANCE": [50000.0, 51500.0, 53500.0]
            }
    */

    private List<String> xAxis;
    private Map<StrategyAnalysisOption, List<Double>> yAxis;
}
