package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class KpRatioParametersDto {

    /*
        KpRatioParameterDto : KP Ratio DB 조회용 Dto
    */

    private LocalDate date;
    private Double profitLossRate;
    private Double accumulatedProfitLossRate;
}
