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
@Table(name = "monthly")
public class Monthly extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "year_number", nullable = false)
    private Integer yearNumber;

    @Column(name = "month_number", nullable = false)
    private Integer monthNumber;

    @Column(name = "average_monthly_principal", nullable = false)
    private Double averageMonthlyPrincipal;

    @Column(name = "profit_loss_amount", nullable = false)
    private Double profitLossAmount;

    @Column(name = "profit_loss_rate", nullable = false)
    private Double profitLossRate;

    @Column(name = "accumulated_profit_loss_amount", nullable = false)
    private Double accumulatedProfitLossAmount;

    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate;

}