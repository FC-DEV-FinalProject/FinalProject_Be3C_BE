package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interest_strategy_log")
public class InterestStrategyLog extends BaseEntity {

    @EmbeddedId
    private InterestStrategyLogId interestStrategyLogId;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", nullable = false)
    private Long logId;

    private String LogCode;

    private String isSendMail;
}
