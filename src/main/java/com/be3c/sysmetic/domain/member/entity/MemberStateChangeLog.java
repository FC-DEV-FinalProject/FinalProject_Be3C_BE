package com.be3c.sysmetic.domain.member.entity;

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
@Table(name = "member_state_change_log")
public class MemberStateChangeLog {
    /*
        id : 회원상태변화이력 식별번호
        member : 상태가 변경된 회원
        email : 회원의 이메일
        manager : 상태를 변경한 관리자 (강제탈퇴된 경우에만 기재 필요)
        stateChangeCode : 회원상태 변화 코드
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "email", nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Member manager;

    @Column(name = "state_change_code", nullable = false)
    private String stateChangeCode;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}