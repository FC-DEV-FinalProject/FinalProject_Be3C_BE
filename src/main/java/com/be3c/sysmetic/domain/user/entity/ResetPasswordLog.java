package com.be3c.sysmetic.domain.user.entity;

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
public class ResetPasswordLog {
    /*
        id : 비밀번호재설정이력 식별번호
        tryIp : 시도한 ip
        member : 시도한 회원 id
        tryDate : 시도일자
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

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}