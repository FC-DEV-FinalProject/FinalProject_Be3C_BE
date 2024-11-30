package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "strategy")
public class Strategy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member trader;

    @ManyToOne
    @JoinColumn(name = "method_id", nullable = false)
    private Method method;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "strategy_name", length = 30, nullable = false)
    private String name;

    @Column(name = "strategy_cycle", nullable = false)
    private Character cycle;

    @Size(max = 500)
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "follower_count", nullable = false)
    private Long followerCount;

    @Column(name = "mdd", nullable = false)
    private Double mdd;

    @Column(name = "kp_ratio", nullable = false)
    private Double kpRatio;

    @Column(name = "sm_score", nullable = false)
    private Double smScore;

    @Column(name = "winning_rate")
    private Double winningRate;

    // 누적수익률 추가
    @Column(name = "accumulated_profit_loss_rate", nullable = false)
    private Double accumulatedProfitLossRate;

    @CreatedDate
    @Column(name = "strategy_created_date", nullable = false)
    private LocalDateTime strategyCreatedDate;

    @LastModifiedDate
    @Column(name = "strategy_modified_date", nullable = false)
    private LocalDateTime strategyModifiedDate;

    // StrategyStockReference 매핑
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StrategyStockReference> stockReferences;

    // StrategyApprovalHistory 매핑
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StrategyApprovalHistory> approvalHistories;

    // StrategyStatistics 매핑
    @OneToOne(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private StrategyStatistics statistics;

    // Monthly 매핑
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Monthly> monthlyData;

    // Daily 매핑
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Daily> dailyData;

    // AccountImage 매핑
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AccountImage> accountImages;

    public void increaseFollowerCount() {
        followerCount++;
    }

    public void decreaseFollowerCount() {
        followerCount--;
    }
}