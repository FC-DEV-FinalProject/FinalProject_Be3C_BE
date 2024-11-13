package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDeleteRequestDto {
    private Long folderId;
    private List<Long> strategyId;
}
