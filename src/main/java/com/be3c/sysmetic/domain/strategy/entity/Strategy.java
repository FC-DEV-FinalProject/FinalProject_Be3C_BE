package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    // ManyToOne -> 연관관계 주인 - Member
    // @ManyToOne
    // @JoinColumn(name = "member_id", nullable = false)
    // private Member trader;

    // @OneToOne
    // @JoinColumn(name = "method_id", nullable = false)
    // private Method method;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "strategyCycle", nullable = false)
    private Character strategyCycle;

    @Column(name = "min_operation_amount", nullable = false)
    private Double minOperationAmount;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "follower_count", nullable = false)
    @ColumnDefault("0")
    private Long followerCount;

    @Column(name = "share_count", nullable = false, columnDefinition = "Integer default 0")
    private Integer shareCount;

    @Column(name = "kp_ratio", columnDefinition = "Double default 0.0")
    private Double kpRatio;

    @Column(name = "sm_score", columnDefinition = "Double default 0.0")
    private Double smScore;

    @Column(name = "strategy_created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime strategyCreatedDate;

    @Column(name = "strategy_modified_date", nullable = false, columnDefinition = "Timestamp default now() on update now()")
    private LocalDateTime strategyModifiedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false, columnDefinition = "Timestamp default now()")
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false,  columnDefinition = "Timestamp default now() on update now()")
    private LocalDateTime modifiedDate;
}