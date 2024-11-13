package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TraderNicknameListDto {

    private long id;
    private String nickname;
    private String roleCode;
    private int totalFollow;
    private Long count;
}
