package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
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

    /*
        StrategyStatistics : 전략 통계

        id : 전략 id
        currentBalance : 잔고
        principal : 원금
        accumulatedDepositWithdrawalAmount : 누적입출금액
        accumulatedProfitLossAmount : 누적손익금액
        accumulatedProfitLossRate : 누적손익률
        currentCapitalReductionAmount : 현재자본인하금액
        currentCapitalReductionRate : 현재자본인하율
        maximumCapitalReductionAmount: 최대자본인하금액
        maximumCapitalReductionRate : 최대자본인하율
        averageProfitLossAmount : 평균손익금액
        averageProfitLossRate : 평균손익률
        maximumDailyProfitAmount : 최대일이익금액
        maximumDailyProfitRate : 최대일이익률
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
        profitFactor :Profit Factor
        roa : ROA
        firstRegistrationDate : 최초등록일시
        lastRegistrationDate : 최종등록일시
    */

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "current_balance", nullable = false)
    private Double currentBalance; // 잔고

    @Column(name = "principal", nullable = false)
    private Double principal; // 원금

    @Column(name = "accumulated_deposit_withdrawal_amount", nullable = false)
    private Double accumulatedDepositWithdrawalAmount; // 누적입출금

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount; // 누적손익금

    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate; // 누적손익률

    @Column(name = "maximum_accumulated_profit_loss_amount", nullable = false)
    private Double maximumAccumulatedProfitLossAmount; // 최대누적손익금

    @Column(name = "maximum_accumulated_profit_loss_rate", nullable = false)
    private Double maximumAccumulatedProfitLossRate; // 최대누적손익율

    @Column(name = "current_capital_reduction_amount", nullable = false)
    private Double currentCapitalReductionAmount; // 현재자본인하금액

    @Column(name = "current_capital_reduction_rate", nullable = false)
    private Double currentCapitalReductionRate; // 현재자본인하율

    @Column(name = "maximum_capital_reduction_amount", nullable = false)
    private Double maximumCapitalReductionAmount; // 최대자본인하금액

    @Column(name = "maximum_capital_reduction_rate", nullable = false)
    private Double maximumCapitalReductionRate; // 최대자본인하율

    @Column(name = "average_profit_loss_amount", nullable = false)
    private Double averageProfitLossAmount; // 평균손익금

    @Column(name = "average_profit_loss_rate", nullable = false)
    private Double averageProfitLossRate; // 평균손익률

    @Column(name = "maximum_daily_profit_amount", nullable = false)
    private Double maximumDailyProfitAmount; // 최대일이익금

    @Column(name = "maximum_daily_profit_rate", nullable = false)
    private Double maximumDailyProfitRate; // 최대일이익율

    @Column(name = "maximum_daily_loss_amount", nullable = false)
    private Double maximumDailyLossAmount; // 최대일손실금

    @Column(name = "maximum_daily_loss_rate", nullable = false)
    private Double maximumDailyLossRate; // 최대일손실율

    @Column(name = "total_trading_days", nullable = false)
    private Long totalTradingDays; // 총매매일수

    @Column(name = "current_continuous_profit_loss_days", nullable = false)
    private Long currentContinuousProfitLossDays; // 현재연속손익일수

    @Column(name = "total_profit_days", nullable = false)
    private Long totalProfitDays; // 총이익일수

    @Column(name = "max_continuous_profit_days", nullable = false)
    private Long maximumContinuousProfitDays; // 최대연속이익일수

    @Column(name = "total_loss_days", nullable = false)
    private Long totalLossDays; // 총손실일수

    @Column(name = "max_continuous_loss_days", nullable = false)
    private Long maximumContinuousLossDays; // 최대연속손실일수

    @Column(name = "winning_rate", nullable = false)
    private Double winningRate; // 승률

    @Column(name = "high_point_renewal_progress", nullable = false)
    private Long highPointRenewalProgress; // 고점갱신후경과일

    @Column(name = "profit_factor", nullable = false)
    private Double profitFactor;

    @Column(name = "roa", nullable = false)
    private Double roa;

    @CreatedDate
    @Column(name = "first_registration_date", nullable = false)
    private LocalDate firstRegistrationDate; // 최초등록일시

    @LastModifiedDate
    @Column(name = "last_registration_date", nullable = false)
    private LocalDate lastRegistrationDate; // 최종등록일시

}