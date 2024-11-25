package com.be3c.sysmetic.domain.strategy.entity;

import com.be3c.sysmetic.domain.member.entity.Member;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Cascade;
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

    @PrePersist
    public void prePersist() {
        followerCount = 0L;
        kpRatio = 0.0;
        smScore = 0.0;
    }

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

    @Column(name = "follower_count")
    private Long followerCount;

    @Column(name = "kp_ratio")
    private Double kpRatio;

    @Column(name = "sm_score")
    private Double smScore;

    @CreatedDate
    @Column(name = "strategy_created_date", nullable = false)
    private LocalDateTime strategyCreatedDate;

    @LastModifiedDate
    @Column(name = "strategy_modified_date", nullable = false)
    private LocalDateTime strategyModifiedDate;

    // StrategyStockReference 매핑
    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StrategyStockReference> stockReferences;

    // StrategyApprovalHistory 매핑
    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<StrategyApprovalHistory> approvalHistories;

    // StrategyStatistics 매핑
    @OneToOne(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private StrategyStatistics statistics;

    // Monthly 매핑
    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Monthly> monthlyData;

    // Daily 매핑
    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Daily> dailyData;

    // AccountImage 매핑
    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<AccountImage> accountImages;

    public void increaseFollowerCount() {
        followerCount++;
    }

    public void decreaseFollowerCount() {
        followerCount--;
    }
}