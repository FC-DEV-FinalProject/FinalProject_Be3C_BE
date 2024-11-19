package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.entity.Stock;
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
        traderId : 트레이더 id
        methodId : 매매방식(운영방법) id
        stockList : 종목 목록
        stock  : 종목명
        name : 전략명
        nickname : 트레이더 닉네임
        methodName : 매매방식명
        cycle : 주기
        followerCount : 해당 전략 팔로우 수
        mdd : MDD
        kpRatio : KP Ratio
        smScore : SM Score
        accumProfitLossRate : 누적 수익률
        maximumCapitalReductionAmount : 최대 자본 인하율
        averageProfitLossRate : 평균 손익률
        profitFactor : Profit Factor
        winningRate : 승률
    */
    private Long id;
    private Long traderId;
    private Long methodId;
    // private List<Stock> stockList;
    private String stock;       // TODO : List<Stock> 으로 수정
    private String name;
    private String nickname;
    private String methodName;
    private String statusCode;
    private Character cycle;
    private String content;
    private Long followerCount;
    private Double mdd;     // MaximumDrawDown 전고점 대비 하락률
    private Double kpRatio;
    private Double smScore;
    private Double accumProfitLossRate;
    private Double maximumCapitalReductionAmount;
    private Double averageProfitLossRate;
    private Double profitFactor;
    private Double winningRate;

    // TODO : 월간 수익률 데이터 추가
    // TODO : 분석 지표 데이터 추가
}