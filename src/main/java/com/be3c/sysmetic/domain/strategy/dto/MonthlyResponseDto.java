package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyResponseDto {
    private Long monthId; // 월간분석 데이터 식별번호
    private String yearMonth; // 년월
    private Double avragePrincipal; // 월 평균 원금
    private Double depositWithdrawalAmount; // 입출금
    private Double profitLossAmount; // 월 손익
    private Double profitLossRate; // 월 손익률
    private Double accumulatedProfitLossAmount; // 누적손익
    private Double accumulatedProfitLossRate; // 누적손익률
}
