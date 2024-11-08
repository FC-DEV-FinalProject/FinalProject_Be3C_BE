package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderGetResponseDto {
    private Long id;
    private String strategyName;
    private String traderName;
    private String[] iconPath;
    private Long followerCount;
    private Double accumulatedProfitRatio;
    private Double SMScore;
    private String MDD;
    private Boolean isFollow;
}
