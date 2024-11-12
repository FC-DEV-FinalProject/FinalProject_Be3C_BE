package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageReplyResponseDto {
    private Long strategyId;
    private Long memberId;
    private String content;
}
