package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.global.common.response.PageResponse;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MyStrategyListResponseDto {

    private Long traderId;
    private String traderNickname;
    private String traderProfileImage;
    private Integer totalfollowers;
    private PageResponse<MyStrategyListDto> strategyList;
}
