package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strategy_approval_history")
public class StrategyApprovalHistory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member manager;

    @ManyToOne
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @CreatedDate
    @Column(name = "strategy_approval_date", nullable = false)
    private LocalDateTime strategyApprovalDate;
}