package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "strategy")
public class Strategy {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member trader;

    @ManyToOne
    @JoinColumn(name = "method_id", unique = false, nullable = false)
    private Method method;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "strategy_name", length = 30, nullable = false)
    private String name;

    @Column(name = "strategy_cycle", nullable = false)
    private Character cycle;

    @Column(name = "min_operation_amount", nullable = false)
    private Double minOperationAmount;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "follower_count", nullable = false)
    private Long followerCount;

    @Column(name = "mdd")
    private Double mdd;

    @Column(name = "kp_ratio")
    private Double kpRatio;

    @Column(name = "sm_score")
    private Double smScore;

    // 누적수익률 추가
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#00.00")
    @Column(name = "accum_profit_rate", nullable = true)
    private Double accumProfitRate;

    @Column(name = "strategy_created_date", nullable = false)
    private LocalDateTime strategyCreatedDate;

    @Column(name = "strategy_modified_date", nullable = false)
    private LocalDateTime strategyModifiedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}