package com.be3c.sysmetic.domain.strategy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyGetResponseDto {

    @Schema(description = "월간분석 식별번호", example = "1")
    @NotNull
    private Long monthId;

    @Schema(description = "년월", example = "2024-10")
    @NotBlank
    private String yearMonth;

    @Schema(description = "월 평균원금", example = "288000000")
    @NotNull
    private Double averagePrincipal;

    @Schema(description = "월 입출금", example = "34068000")
    @NotNull
    private Double depositWithdrawalAmount;

    @Schema(description = "월 손익금", example = "-435000")
    @NotNull
    private Double profitLossAmount;

    @Schema(description = "월 손익률", example = "-12.2041%")
    @NotNull
    private Double profitLossRate;

    @Schema(description = "누적손익금", example = "64500000")
    @NotNull
    private Double accumulatedProfitLossAmount;

    @Schema(description = "누적손익률", example = "21.8333%")
    @NotNull
    private Double accumulatedProfitLossRate;

}
