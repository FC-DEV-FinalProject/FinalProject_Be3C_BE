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
@Table(name = "daily")
public class Daily {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "principal", nullable = false)
    private Double principal;

    @Column(name = "deposit_withdrawal_amount", nullable = false)
    private Double depositWithdrawalAmount;

    @Column(name = "daily_loss_amount", nullable = false)
    private Double dailyLossAmount;

    @Column(name = "daily_loss_rate", nullable = false)
    private Double dailyLossRate;

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount;

    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false, columnDefinition = "Timestamp default now() on update now()")
    private LocalDateTime modifiedDate;
}