package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class StrategyDetailDto {

    /*
        StrategyDetailDto : 전략 상세 페이지 응답 Dto

        id : 전략 id
        traderNickname : 트레이더 닉네임
        methodId : 매매방식(운영방법) id
        methodName : 매매방식 명
        stockList : 종목 목록
        stock  : 종목명
        name : 전략명
        cycle : 주기
        followerCount : 해당 전략 팔로우 수
        mdd : MDD
        kpRatio : KP Ratio
        smScore : SM Score
        accumulatedProfitLossRate : 누적 수익률
        maximumCapitalReductionAmount : 최대 자본 인하율
        averageProfitLossRate : 평균 손익률
        profitFactor : Profit Factor
        winningRate : 승률
        monthlyRecord : 월간 손익률
        analysis : 분석 지표
    */
    private Long id;
    private String traderNickname;
    private Long methodId;
    private String methodName;
    private StockListDto stockList;
    private String name;
    private String statusCode;
    private Character cycle;
    private String content;
    private Long followerCount;
    private Double mdd;
    private Double kpRatio;
    private Double smScore;
    private Double accumulatedProfitLossRate;
    private Double maximumCapitalReductionAmount;
    private Double averageProfitLossRate;
    private Double profitFactor;
    private Double winningRate;
    private List<MonthlyRecord> monthlyRecord;
    private StrategyAnalysisResponseDto analysis;
}