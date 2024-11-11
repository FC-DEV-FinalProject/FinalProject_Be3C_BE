package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TraderListDto {

    private Long traderId;
    private String nickname;
    private Long followerCount;
    // 프로필 사진
}