package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strategy_graph_analysis")
public class StrategyGraphAnalysis extends BaseEntity {


    /*

        StrategyGraphAnalysis : 전략 상세 페이지 내 그래프 데이터 & 메인 페이지 그래프 데이터 (날짜별 매 행 데이터 필요)
        id : id
        strategy : 전략
        daily : 일간 데이터
        date : 날짜
        standardAmount : 기준가
        currentBalance : 잔고
        principal : 원금
        accumulatedDepositWithdrawalAmount : 누적입출금액
        depositWithdrawalAmount : 일별입출금액
        profitLossAmount : 손익금
        profitLossRate : 손익률
        accumulatedProfitLossAmount : 누적손익금
        currentCapitalReductionAmount : 현재자본인하금액
        currentCapitalReductionRate : 현재자본인하율
        averageProfitLossAmount : 평균손익금액
        averageProfitLossRate : 평균손익률
        winningRate : 승률
        profitFactor : Profit Factor
        roa : ROA
        maximumCapitalReductionAmount : 최대자본인하금액
        totalProfit : 총이익
        totalLoss : 총손실
    */

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id",nullable = false)
    private Strategy strategy;

    @OneToOne
    @JoinColumn(name = "daily_id", nullable = false)
    private Daily daily;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "standard_amount", nullable = false)
    private Double standardAmount;

    @Column(name = "current_balance", nullable = false)
    private Double currentBalance;

    @Column(name = "principal", nullable = false)
    private Double principal;

    @Column(name = "accumulated_deposit_withdrawal_amount", nullable = false)
    private Double accumulatedDepositWithdrawalAmount;

    @Column(name = "deposit_withdrawal_amount", nullable = false)
    private Double depositWithdrawalAmount;

    @Column(name = "profit_loss_amount", nullable = false)
    private Double profitLossAmount;

    @Column(name = "profit_loss_rate", nullable = false)
    private Double profitLossRate;

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount;

    @Column(name = "current_capital_reduction_amount", nullable = false)
    private Double currentCapitalReductionAmount;

    @Column(name = "current_capital_reduction_rate", nullable = false)
    private Double currentCapitalReductionRate;

    @Column(name = "average_profit_loss_amount", nullable = false)
    private Double averageProfitLossAmount;

    @Column(name = "average_profit_loss_rate", nullable = false)
    private Double averageProfitLossRate;

    @Column(name = "winning_rate", nullable = false)
    private Double winningRate;

    @Column(name = "profit_factor", nullable = false)
    private Double profitFactor;

    @Column(name = "roa", nullable = false)
    private Double roa;

    @Column(name = "maximum_capital_reduction_amount", nullable = false)
    private Double maximumCapitalReductionAmount;
}
