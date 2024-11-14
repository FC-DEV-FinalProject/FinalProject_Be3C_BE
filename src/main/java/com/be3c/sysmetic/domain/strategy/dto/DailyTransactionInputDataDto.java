package com.be3c.sysmetic.domain.strategy.dto;

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
}