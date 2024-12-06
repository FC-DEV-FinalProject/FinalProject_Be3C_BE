package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class YearlyRecord {

    /*
        YearlyRecord : 전략 상세 페이지 월간 누적 손익률 응답용 DTO
    */

    private Integer year;
    private List<MonthlyRecord> data;
}
