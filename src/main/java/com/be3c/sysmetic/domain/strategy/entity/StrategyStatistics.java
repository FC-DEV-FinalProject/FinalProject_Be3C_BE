package com.be3c.sysmetic.domain.strategy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strategy_statistics")
public class StrategyStatistics {

    /*
    id : 전략 통계 id
    strategy : 전략
    currentBalance : 잔고
    principal : 원금
    accumulatedDepositWithdrawalAmount : 누적입출금액
    accumulatedProfitLossAmount : 누적손익금액
    accumulatedProfitLossRate : 누적손익률
    accumulatedProfitAmount : 누적수익금액
    accumulatedProfitRate : 누적수익률
    currentCapitalReductionAmount : 현재자본인하금액
    currentCapitalReductionRate : 현재자본인하율
    maximumCapitalReductionAmount : 최대자본인하금액
    maximumCapitalReductionRate : 최대자본인하율
    averageProfitLossAmount : 평균손익금액
    averageProfitLossRate : 평균손익률
    maximumDailyProfitAmount : 최대일수익금액
    maximumDailyProfitRate : 최대일수익률
    maximumDailyLossAmount : 최대일손실금액
    maximumDailyLossRate : 최대일손실율
    totalTradingDays : 총매매일수
    currentContinuousProfitLossDays : 현재연속손익일수
    totalProfitDays : 총이익일수
    maxContinuousProfitDays : 최대연속이익일수
    totalLossDays : 총손실일수
    maxContinuousLossDays : 최대연속손실일수
    winningRate : 승률
    highPointRenewalProgress : 고점갱신후경과일
    profitFactor
    roa
    lastYearProfitRate : 최근1년수익률
    standardDeviation : 표준편차
    firstRegistrationDate : 최초등록일시
    lastRegistrationDate : 최종등록일시
     */

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        modifiedDate = now;
        firstRegistrationDate = now;
        lastRegistrationDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        modifiedDate = now;
        lastRegistrationDate = now;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "current_balance", nullable = false)
    private Double currentBalance;

    @Column(name = "principal", nullable = false)
    private Double principal;

    @Column(name = "accumulated_deposit_withdrawal_amount", nullable = false)
    private Double accumulatedDepositWithdrawalAmount;

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount;

    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate;

    @Column(name = "accumulated_profit_amount", nullable = false)
    private Double accumulatedProfitAmount;

    @Column(name = "accumulated_profit_rate", nullable = false)
    private Double accumulatedProfitRate;

    @Column(name = "current_capital_reduction_amount", nullable = false)
    private Double currentCapitalReductionAmount;

    @Column(name = "current_capital_reduction_rate", nullable = false)
    private Double currentCapitalReductionRate;

    @Column(name = "maximum_capital_reduction_amount", nullable = false)
    private Double maximumCapitalReductionAmount;

    @Column(name = "maximum_capital_reduction_rate", nullable = false)
    private Double maximumCapitalReductionRate;

    @Column(name = "average_profit_loss_amount", nullable = false)
    private Double averageProfitLossAmount;

    @Column(name = "average_profit_loss_rate", nullable = false)
    private Double averageProfitLossRate;

    @Column(name = "maximum_daily_profit_amount", nullable = false)
    private Double maximumDailyProfitAmount;

    @Column(name = "maximum_daily_profit_rate", nullable = false)
    private Double maximumDailyProfitRate;

    @Column(name = "maximum_daily_loss_amount", nullable = false)
    private Double maximumDailyLossAmount;

    @Column(name = "maximum_daily_loss_rate", nullable = false)
    private Double maximumDailyLossRate;

    @Column(name = "total_trading_days", nullable = false)
    private Long totalTradingDays;

    @Column(name = "current_continuous_profit_loss_days", nullable = false)
    private Long currentContinuousProfitLossDays;

    @Column(name = "total_profit_days", nullable = false)
    private Long totalProfitDays;

    @Column(name = "max_continuous_profit_days", nullable = false)
    private Long maxContinuousProfitDays;

    @Column(name = "total_loss_days", nullable = false)
    private Long totalLossDays;

    @Column(name = "max_continuous_loss_days", nullable = false)
    private Long maxContinuousLossDays;

    @Column(name = "winning_rate", nullable = false)
    private Double winningRate;

    @Column(name = "high_point_renewal_progress", nullable = false)
    private LocalDateTime highPointRenewalProgress;

    @Column(name = "profit_factor", nullable = false)
    private Double profitFactor;

    @Column(name = "roa", nullable = false)
    private Double roa;

    @Column(name = "last_year_profit_rate", nullable = false)
    private Double lastYearProfitRate;

    @Column(name = "standard_deviation", nullable = false)
    private Double standardDeviation;

    @Column(name = "first_registration_date", nullable = false)
    private LocalDateTime firstRegistrationDate;

    @Column(name = "last_registration_date", nullable = false)
    private LocalDateTime lastRegistrationDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

}