package com.be3c.sysmetic.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
//@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member {
    /*
        id : 회원의 식별번호
        roleCode : 회원등급코드
        email : 이메일
        password : 비밀번호
        name : 이름
        nickname : 닉네임
        phoneNumber : 휴대폰 번호
        usingStatusCode : 회원상태코드
        totalFollow : 총 팔로워 수
        receiveInfoConsent : 정보성 수신 동의 여부
        infoConsentDate : 정보성 수신 동의일
        receiveMarketingConsent : 마케팅 수신 동의 여부
        marketingConsentDate : 마케팅 수신 동의일
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "using_status_code", nullable = false)
    private String usingStatusCode;

    @Column(name = "total_follow", nullable = false)
    private Integer totalFollow;

    @Column(name = "total_strategy_count", nullable = false)
    private Integer totalStrategyCount;

    @Column(name = "receive_info_consent", nullable = false)
    private String receiveInfoConsent;

    @Column(name = "info_consent_date", nullable = false)
    private LocalDateTime infoConsentDate;

    @Column(name = "receive_marketing_consent", nullable = false)
    private String receiveMarketingConsent;

    @Column(name = "marketing_consent_date", nullable = false)
    private LocalDateTime marketingConsentDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_by", nullable = false)
    private Long modifiedBy;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;
}