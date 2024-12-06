package com.be3c.sysmetic.domain.strategy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TraderRankingDto {

    /*
        TraderRankingDto : 메인 페이지의 인기 트레이더 랭킹 응답 Dto

        id : 트레이더 id
        nickname : 트레이더 닉네임
        traderProfileImage : 트레이더 프로필 이미지
        followerCount : 총 팔로우 수
        accumProfitLossRate : 누적손익률
    */

    private Long id;
    private String nickname;
    private String traderProfileImage;
    private Long followerCount;
    private Double accumProfitLossRate;
}
