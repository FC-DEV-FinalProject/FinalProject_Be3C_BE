package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folder")
public class Folder extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

    @Column(name = "folder_description")
    private String folderDescription;

    @Column(name = "internal_interest_strategy_count")
    private Integer internalInterestStrategyCount;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "latest_interest_strategy_added_date")
    private LocalDateTime latestInterestStrategyAddedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
    private List<InterestStrategy> interestStrategies;
}
