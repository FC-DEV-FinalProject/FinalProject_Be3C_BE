package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
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
public class StrategyStatistics extends BaseEntity {

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
}