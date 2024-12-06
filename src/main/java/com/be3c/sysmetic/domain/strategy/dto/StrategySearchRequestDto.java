package com.be3c.sysmetic.domain.strategy.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StrategySearchRequestDto {

    /*
        SearchConditionRequestDto : 전략 상세 조건 검색 요청 Dto

       methods : 매매방식
       cycle : 주기
       stockNames : 종목
       period : 기간
       accumulatedProfitLossRate : 누적수익률
    */

    private List<String> methods;
    private List<String> cycle;
    private List<String> stockNames;
    private String period;
    private String accumulatedProfitLossRateRangeStart;
    private String accumulatedProfitLossRateRangeEnd;


    @JsonCreator
    public StrategySearchRequestDto(
            @JsonProperty("methods") List<String> methods,
            @JsonProperty("cycle") List<String> cycle,
            @JsonProperty("stockNames") List<String> stockNames,
            @JsonProperty("period") String period,
            @JsonProperty("accumulatedProfitLossRateRangeStart") String accumulatedProfitLossRateRangeStart,
            @JsonProperty("accumulatedProfitLossRateRangeEnd") String accumulatedProfitLossRateRangeEnd) {
        this.methods = methods;
        this.cycle = cycle;
        this.stockNames = stockNames;
        this.period = period;
        this.accumulatedProfitLossRateRangeStart = accumulatedProfitLossRateRangeStart;
        this.accumulatedProfitLossRateRangeEnd = accumulatedProfitLossRateRangeEnd;
    }
}
