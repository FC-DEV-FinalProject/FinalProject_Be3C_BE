package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraderNicknameListDto {

    /*
        TraderNicknameListDto : 트레이더 닉네임으로 검색 결과 응답 Dto

        id : 트레이더 id
        nickname : 트레이더 닉네임
        roleCode : 회원 등급 구분
        totalFollow : 총 팔로우 수
        count : 공개중인 전략 개수
    */

    private long id;
    private String nickname;
    private String roleCode;
    private int totalFollow;
    private Long count;
}
