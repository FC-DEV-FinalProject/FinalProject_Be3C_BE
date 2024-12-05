package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.global.util.file.dto.FileWithInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class StrategyDetailDto {

    /*
        StrategyDetailDto : 전략 상세 페이지 응답 Dto

        id : 전략 id
        traderId : 트레이더 PK
        traderNickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 이미지
        methodId : 매매방식 id
        methodName : 매매방식 이름
        methodIconPath : 매매방식 아이콘 경로
        stockName : 종목 이름
        stockIconPath : 종목 아이콘 경로
        isFollow : 관심전략 추가 여부
        name : 전략명
        content : 전략 소개
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
    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private Long methodId;
    private String methodName;
    private String methodIconPath;
    private StockListDto stockList;
    private Boolean isFollow;
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
    private List<YearlyRecord> monthlyRecord;
    private StrategyAnalysisResponseDto analysis;

    private FileWithInfoResponse fileWithInfoResponse;
}