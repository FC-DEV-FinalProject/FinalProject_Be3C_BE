package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.validation.ValidDailyDate;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaveDailyRequestDto {

    @NotNull
    private Long strategyId;

    private Long dailyId; // nullable, 수정시 사용

    @ValidDailyDate
    @NotNull
    private LocalDateTime date;

    @NotNull
    private Double depositWithdrawalAmount;

    @NotNull
    private Double dailyProfitLossAmount;
}
