package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Schema(description = "관심 전략 조회 응답 DTO")
public class MyStrategyGetResponseDto {

    @Schema(description = "전략 ID", example = "1")
    private Long id;

    @Schema(description = "전략 이름", example = "효율적인 트레이딩 전략")
    private String strategyName;

    @Schema(description = "트레이더 이름", example = "Jane Doe")
    private String traderName;

    @Schema(description = "아이콘 경로 리스트", example = "[\"/path/to/icon1.png\", \"/path/to/icon2.png\"]")
    private List<String> iconPath;

    @Schema(description = "팔로워 수", example = "1200")
    private Long followerCount;

    @Schema(description = "누적 수익률", example = "25.75")
    private Double accumulatedProfitRatio;

    @Schema(description = "SM 점수", example = "88.5")
    private Double SMScore;

    @Schema(description = "최대 낙폭(MDD)", example = "-12.34")
    private Double MDD;

    // Hibernate에서 사용할 생성자
    public MyStrategyGetResponseDto(Long id, String strategyName, String traderName, List<String> iconPath,
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
