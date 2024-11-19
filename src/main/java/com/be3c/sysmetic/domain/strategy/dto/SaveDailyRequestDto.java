package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.validation.ValidDailyDate;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SaveDailyRequestDto {

    @ValidDailyDate
    @NotNull
    private LocalDate date;

    @NotNull
    private Double depositWithdrawalAmount;

    @NotNull
    private Double dailyProfitLossAmount;
}
