package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StrategyListDto {

    /*
        StrategyListDto : 전략 목록 응답 Dto

        strategyId : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 이미지 경로
        methodIconPath : 매매 유형 아이콘
        methodName : 매매방식명
        name : 전략 명
        cycle : 주기
        stockList : 종목 리스트
        accumulatedProfitLossRate : 누적수익률
        mdd : MDD
        smScore : SM Score
    */
    private Long strategyId;
    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private Long methodId;
    private String methodIconPath;
    private String name;
    private Character cycle;
    private StockListDto stockList;
    private Boolean isFollow;
    private Double accumulatedProfitLossRate;
    private Double mdd;
    private Double smScore;

    public static StrategyListDto getStrategyListDto(Strategy strategy, StockListDto stockList, String traderProfileImage, String methodIconPath) {
        return StrategyListDto.builder()
                .strategyId(strategy.getId())
                .traderId(strategy.getTrader().getId())
                .traderNickname(strategy.getTrader().getNickname())
                .traderProfileImage(traderProfileImage)
                .methodId(strategy.getMethod().getId())
                .methodIconPath(methodIconPath)
                .name(strategy.getName())
                .cycle(strategy.getCycle())
                .stockList(stockList)
                .isFollow(false)
                .accumulatedProfitLossRate(strategy.getAccumulatedProfitLossRate())
                .mdd(strategy.getMdd())
                .smScore(strategy.getSmScore())
                .build();
    }
}