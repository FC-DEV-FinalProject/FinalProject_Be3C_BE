package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class FolderGetResponseDto {
    private Long id;
    private String strategyName;
    private String traderName;
    private List<String> iconPath;
    private Long followerCount;
    private Double accumulatedProfitRatio;
    private Double SMScore;
    private Double MDD;

    // Hibernate에서 사용할 생성자
    public FolderGetResponseDto(Long id, String strategyName, String traderName, List<String> iconPath,
                                Long followerCount, Double accumulatedProfitRatio, Double SMScore, Double MDD) {
        this.id = id;
        this.strategyName = strategyName;
        this.traderName = traderName;
        this.iconPath = iconPath;
        this.followerCount = followerCount;
        this.accumulatedProfitRatio = accumulatedProfitRatio;
        this.SMScore = SMScore;
        this.MDD = MDD;
    }
}
