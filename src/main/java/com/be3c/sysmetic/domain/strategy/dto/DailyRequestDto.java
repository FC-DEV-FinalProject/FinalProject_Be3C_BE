package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.validation.ValidDailyDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DailyRequestDto {

    @Schema(description = "일간분석 일자", example = "2024-11-18")
    @ValidDailyDate
    @NotNull
    private LocalDate date;

    @Schema(description = "입출금", example = "360000")
    @NotNull
    private Double depositWithdrawalAmount;

    @Schema(description = "손익금", example = "240000")
    @NotNull
    private Double dailyProfitLossAmount;
    
}
