package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KpRatios {

    /*
        KpRatios : SM Score 계산 시 필요한 KP Ratio DTO
    */

    private Long strategyId;
    private Double kpRatio;
}
