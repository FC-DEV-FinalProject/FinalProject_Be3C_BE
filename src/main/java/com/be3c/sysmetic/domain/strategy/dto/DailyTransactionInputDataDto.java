package com.be3c.sysmetic.domain.strategy.dto;

import com.be3c.sysmetic.domain.strategy.entity.Daily;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;

import java.time.LocalDate;

/**
 * 전략에 대한 일간 데이터
 * @param date 거래일
 * @param depositWithdrawalAmount 입출금
 * @param profitLossAmount 일 손익금 (+수익금액 -손실금액)
 */
public record DailyTransactionInputDataDto(
        LocalDate date,
        Double depositWithdrawalAmount,
        Double profitLossAmount
) {
    public Daily toEntity(Strategy strategy) {  // fields are not nullable
        return Daily.builder()
                .strategy(strategy)
                .date(this.date)
                .depositWithdrawalAmount(this.depositWithdrawalAmount)
                .profitLossAmount(this.profitLossAmount)
                .principal(0.0)
                .currentBalance(0.0)
                .standardAmount(0.0)
                .profitLossRate(0.0)
                .accumulatedProfitLossAmount(0.0)
                .accumulatedProfitLossRate(0.0)
                .build();
    }
}