package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TraderListDto {

    /*
        TraderListDto : 트레이더 닉네임으로 검색 응답 Dto

        traderId : 트레이더 id
        nickname : 트레이더 닉네임
        followerCount : 팔로워 수
    */

    private Long traderId;
    private String nickname;
    private Long followerCount;
    // 트레이더가 등록한 전략 수
    private Long strategyCount;
}