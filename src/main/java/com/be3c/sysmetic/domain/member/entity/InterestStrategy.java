package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interest_strategy")
public class InterestStrategy extends BaseEntity {

    @EmbeddedId
    private FollowStrategyId id;

    @MapsId("folderId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id", nullable = false)
    private Folder folder;

    @MapsId("strategyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @Column(name = "status_code", nullable = false, length = 20)
    private String StatusCode;
}

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
class FollowStrategyId implements Serializable {

    @Column(name = "folder_id", nullable = false)
    private Long folderId;

    @Column(name = "strategy_id", nullable = false)
    private Long strategyId;
}