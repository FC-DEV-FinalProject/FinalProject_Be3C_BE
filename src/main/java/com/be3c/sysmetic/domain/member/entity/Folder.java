package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    /*
        id : PK
        member_id : 멤버 FK
        folder_description : 폴더 설명
        internal_interest_strategy_count : 갖고 있는 관심 전략 수
        latestInterestStrategyAddedDate : 마지막 관심 전략 추가일
        status_code : 상태 코드 (사용중 : US001 / 사용 X : US002 )
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "folder_name", nullable = false)
    private String name;

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

    @OneToMany(mappedBy = "folder", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE) // 외래 키에 ON DELETE CASCADE 적용
    private List<InterestStrategy> interestStrategies;
}
