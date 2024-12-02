package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.dto.StrategyStatusCode;
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

    @Schema(description = "트레이더 id", example = "1")
    private Long traderId;

    @Schema(description = "트레이더 이름", example = "Jane Doe")
    private String traderName;

    @Schema(description = "프로필 사진 경로 리스트", example = "[\"/path/to/icon1.png\", \"/path/to/icon2.png\"]")
    private String traderProfileImage;

    @Schema(description = "매매 유형 아이콘 경로를 위한 매매 유형 아이디", example = "[\"/path/to/icon1.png\", \"/path/to/icon2.png\"]")
    private Long methodId;

    @Schema(description = "매매 방식 아이콘 경로", example = "[\"/path/to/icon1.png\", \"/path/to/icon2.png\"]")
    private String methodIconPath;

    @Schema(description = "종목 아이콘 경로 리스트", example = "[\"/path/to/icon1.png\", \"/path/to/icon2.png\"]")
    private List<String> stockIconPath;

    @Schema(description = "종목 상태 코드", example = "PUBLIC")
    private String statusCode;

    @Schema(description = "팔로워 수", example = "1200")
    private Long followerCount;

    @Schema(description = "SM 점수", example = "88.5")
    private Double SMScore;

    @Schema(description = "누적 수익률", example = "25.75")
    private Double accumulatedProfitRatio;

    @Schema(description = "최대 낙폭(MDD)", example = "-12.34")
    private Double MDD;

    public MyStrategyGetResponseDto(Long id, String strategyName, Long traderId, String traderName, String traderProfileImage, Long methodId, String methodIconPath, List<String> stockIconPath, String statusCode, Long followerCount, Double SMScore, Double accumulatedProfitRatio, Double MDD) {
        this.id = id;
        this.strategyName = strategyName;
        this.traderId = traderId;
        this.traderName = traderName;
        this.traderProfileImage = traderProfileImage;
        this.methodId = methodId;
        this.methodIconPath = methodIconPath;
        this.stockIconPath = stockIconPath;
        this.statusCode = statusCode;
        this.followerCount = followerCount;
        this.accumulatedProfitRatio = accumulatedProfitRatio;
        this.SMScore = SMScore;
        this.MDD = MDD;
    }
}
