package com.be3c.sysmetic.domain.strategy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StrategyAnalysisResponseDto {

    /*
        StrategyDetailGraph - 전략 상세 분석 지표

        xAxis : x축 데이터 - 날짜
        STANDARD_AMOUNT : 기준가
        CURRENT_BALANCE : 잔고
        PRINCIPAL : 원금
        ACCUMULATED_DEPOSIT_WITHDRAWAL_AMOUNT : 누적 입출 금액
        DEPOSIT_WITHDRAWAL_AMOUNT : 일별 입출 금액
        DAILY_PROFIT_LOSS_AMOUNT : 일 손익 금액
        DAILY_PROFIT_LOSS_RATE : 일 손익율
        ACCUMULATED_PROFIT_LOSS_AMOUNT : 누적 손익 금액
        CURRENT_CAPITAL_REDUCTION_AMOUNT : 현재 자본 인하 금액
        CURRENT_CAPITAL_REDUCTION_RATE : 현재 자본 인하율
        AVERAGE_PROFIT_LOSS_AMOUNT : 평균 손익 금액
        AVERAGE_PROFIT_LOSS_RATE : 평균 손익률
        WINNING_RATE : 승률
        PROFIT_FACTOR : Profit Factor
        ROA : ROA
     */

    private List<String> xAxis;
    private List<Double> standardAmounts;
    private List<Double> currentBalance;
    private List<Double> principal;
    private List<Double> accumulatedDepositWithdrawalAmount;
    private List<Double> depositWithdrawalAmount;
    private List<Double> dailyProfitLossAmount;
    private List<Double> dailyProfitLossRate;
    private List<Double> accumulatedProfitLossAmount;
    private List<Double> currentCapitalReductionAmount;
    private List<Double> currentCapitalReductionRate;
    private List<Double> averageProfitLossAmount;
    private List<Double> averageProfitLossRate;
    private List<Double> winningRate;
    private List<Double> profitFactor;
    private List<Double> roa;
}
