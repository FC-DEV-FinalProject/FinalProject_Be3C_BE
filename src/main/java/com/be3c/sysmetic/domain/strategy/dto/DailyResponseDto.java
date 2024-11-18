package com.be3c.sysmetic.domain.strategy.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponseDto {
    private Long dailyId; // 일간분석 데이터 식별번호
    private LocalDateTime date; // 날짜
    private Double principal; // 원금
    private Double depositWithdrawalAmount; // 입출금
    private Double profitLossAmount; // 일 손익
    private Double profitLossRate; // 일 손익률
    private Double accumulatedProfitLossAmount; // 누적손익
    private Double accumulatedProfitLossRate; // 누적손익률
}
