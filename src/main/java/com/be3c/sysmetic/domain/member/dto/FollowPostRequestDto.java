package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowPostRequestDto {
    private Long folderId;
    private Long strategyId;
}
