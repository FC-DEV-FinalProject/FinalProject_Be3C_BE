package com.be3c.sysmetic.domain.strategy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        modifiedDate = now;
        dailyCreatedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime now = LocalDateTime.now();
        modifiedDate = now;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "principal", nullable = false)
    private Double principal;

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

    @Column(name = "daily_created_date", nullable = false)
    private LocalDateTime dailyCreatedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

}