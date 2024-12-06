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
@Table(name = "daily")
public class Daily extends BaseEntity implements Comparable<Daily> {

    /*
        Daily : 일간 데이터

        id : Daily id
        strategy : 전략
        date : 데이터 날짜
        principal : 원금
        currentBalance : 잔고
        standardAmount : 기준가
        depositWithdrawalAmount : 입출금금액
        profitLossAmount : 손익금액
        profitLossRate : 손익율
        accumulatedProfitLossAmount : 누적 손익 금액
        accumulatedProfitLossRate : 누적 손익율
    */

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "principal", nullable = false)
    private Double principal;

    @Column(name = "current_balance", nullable = false)
    private Double currentBalance;

    @Column(name = "standard_amount", nullable = false)
    private Double standardAmount;

    @Column(name = "deposit_withdrawal_amount", nullable = false)
    private Double depositWithdrawalAmount;

    @Column(name = "profit_loss_amount", nullable = false)
    private Double profitLossAmount;

    @Column(name = "profit_loss_rate", nullable = false)
    private Double profitLossRate;

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount;

    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate;


    @Override
    public int compareTo(Daily other) {
        return this.date.compareTo(other.date);
    }
}