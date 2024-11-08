package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "folder")
public class Folder extends BaseEntity {

    @EmbeddedId
    private FolderId id;

    @Column(name = "folder_name", nullable = false)
    private String folderName;

    @Column(name = "folder_description")
    private String folderDescription;

    @Column(name = "internal_interest_strategy_count")
    private Integer internalInterestStrategyCount;

    @Column(name = "usage_status", nullable = false)
    private String usageStatus;

    @Column(name = "latest_interest_strategy_added_date")
    private LocalDateTime latestInterestStrategyAddedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY)
    private List<InterestStrategy> interestStrategies;
}


@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
class FolderId implements Serializable {

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "folder_id")
    private Long folderId;
}