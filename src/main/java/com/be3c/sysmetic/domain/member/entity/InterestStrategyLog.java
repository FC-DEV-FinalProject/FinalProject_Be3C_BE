package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_strategy_id", nullable = false)
    private InterestStrategy interestStrategy;

    @Column(name = "log_code", nullable = false)
    private String LogCode;

    @Column(name = "is_send_mail", nullable = false)
    private String isSendMail;
}
