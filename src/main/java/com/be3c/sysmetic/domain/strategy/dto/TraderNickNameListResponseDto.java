package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraderNickNameListResponseDto {

    /*
        TraderNicknameListDto : 트레이더 닉네임으로 검색 결과 응답 Dto

        traderNicknameListDto : DB에서 조회한 TraderNicknameListDto
        traderProfileImage : 트레이더 프로필 이미지 경로
    */

    private TraderNicknameListDto nicknameListDto;
    private String traderProfileImage;
}