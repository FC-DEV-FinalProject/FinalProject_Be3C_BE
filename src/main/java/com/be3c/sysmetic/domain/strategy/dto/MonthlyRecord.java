package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MonthlyRecord {

    /*
        MonthlyRecord : 전략 상세 페이지의 월간 손익률 응답용 DTO
    */
    private Integer month;
    private Double value;
}
