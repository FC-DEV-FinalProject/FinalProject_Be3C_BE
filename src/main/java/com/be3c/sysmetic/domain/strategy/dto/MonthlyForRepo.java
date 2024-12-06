package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MonthlyForRepo {

    /*
        MonthlyRecord : 전략 상세 페이지의 월간 손익률 DB 조회용 DTO
    */
    private Integer year;
    private Integer month;
    private Double accumulatedProfitLossRate;
}
