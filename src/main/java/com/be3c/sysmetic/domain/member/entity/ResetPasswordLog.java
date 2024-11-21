package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
//@Builder(toBuilder = true)
@NoArgsConstructor
//@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@AllArgsConstructor
@Entity
@Table(name = "reset_password_log")
public class ResetPasswordLog extends BaseEntity {
    /*
        id : 비밀번호재설정이력 식별번호
        tryIp : 시도한 ip
        member : 시도한 회원 id
        tryDate : 시도일시
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "try_ip", nullable = false)
    private String tryIp;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "try_date", nullable = false)
    private LocalDateTime tryDate;

}

