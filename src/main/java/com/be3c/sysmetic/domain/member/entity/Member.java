package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.domain.strategy.entity.Reply;
import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    /*
        id : 회원의 식별번호
        roleCode : 회원등급코드
        email : 이메일
        password : 비밀번호
        name : 이름
        nickname : 닉네임
        birth : 생년월일
        phoneNumber : 휴대폰 번호
        usingStatusCode : 회원상태코드
        totalFollow : 총 팔로워 수
        receiveInfoConsent : 정보성 수신 동의 여부
        infoConsentDate : 정보성 수신 동의일
        receiveMarketingConsent : 마케팅 수신 동의 여부
        marketingConsentDate : 마케팅 수신 동의일
     */

    @PrePersist
    public void prePersist() {
        usingStatusCode = "US001";  // 유효: US001, 휴면: US002
        totalFollow = 0;
    }

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

    @Column(name = "birth", nullable = false)
    private LocalDate birth;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE) // 외래 키에 ON DELETE CASCADE 적용
    private List<Folder> folders;

    @OneToMany(mappedBy = "trader", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE) // 외래 키에 ON DELETE CASCADE 적용
    private List<Strategy> strategies;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE) // 외래 키에 ON DELETE CASCADE 적용
    private List<Reply> replies;
}
