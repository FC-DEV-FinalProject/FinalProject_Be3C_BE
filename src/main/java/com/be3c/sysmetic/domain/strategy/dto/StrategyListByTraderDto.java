package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StrategyListByTraderDto {
    /*

        StrategyListByTraderDto : 트레이더별 전략 목록 응답용 DTO

        strategyId : 전략 id
        traderId : 트레이더 id
        traderNickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 이미지
        methodId : 매매방식 id
        methodName : 매매방식명
        stockList : 종목 리스트
        strategyId : 전략 ID
        strategyName : 전략명
        cycle : 주기
        followerCount : 팔로우 수
        strategyCount : 등록한 전략 개수
        accumulatedProfitLossRate : 누적손익률
        mdd : MDD
        smScore : SM Score
     */
    // 12월 2일
    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private Integer followerCount;
    private Integer strategyCount;
    PageResponse<TraderStrategyListDto> strategyListDto;
}