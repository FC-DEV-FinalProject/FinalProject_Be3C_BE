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
@Table(name = "monthly")
public class Monthly {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "month_id", nullable = false)
    private Integer month;

    @Column(name = "average_monthly_wage", nullable = false)
    private Double averageMonthlyWage;

    @Column(name = "monthly_profit_loss_amount", nullable = false)
    private Double monthlyProfitLossAmount;

    @Column(name = "monthly_profit_rate", nullable = false)
    private Double monthlyProfitRate;

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