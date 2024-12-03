package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StrategyAlgorithmResponseDto {
    /*
        StrategyAlgorithmResposneDto : 전략 알고리즘 검색 응답 Dto

        algorithmType : 검색한 알고리즘 타입 이름
        id : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 사진 경로
        methodIconPath : 매매 방식 아이콘 경로
        stockIconPath : 종목 아이콘 경로
        name : 전략명
        cycle : 주기
        accumulatedProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */
    private StrategyAlgorithmOption algorithm;
    private Long id;
    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private String methodIconPath;
    private List<String> stockIconPath;
    private String name;
    private Character cycle;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;
}
