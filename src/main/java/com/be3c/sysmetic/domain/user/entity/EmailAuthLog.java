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
@Table(name = "email_auth_log")
public class EmailAuthLog {
    /*
        id : 이메일인증이력 식별번호
        tryIp : 시도한 ip
        receiveEmail : 사용자가 입력한 이메일
        authCode : 인증코드번호
        authStatusCode : 인증상태코드
        expiredDate : 만료일시
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "try_ip", nullable = false)
    private String tryIp;

    @Column(name = "receive_email", nullable = false)
    private String receiveEmail;

    @Column(name = "auth_code", nullable = false)
    private String authCode;

    @Column(name = "auth_status_code", nullable = false)
    private String authStatusCode;

    @Column(name = "expired_date", nullable = false)
    private LocalDateTime expiredDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}