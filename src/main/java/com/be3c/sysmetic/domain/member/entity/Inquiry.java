package com.be3c.sysmetic.domain.member.entity;

import com.be3c.sysmetic.domain.strategy.entity.Strategy;
import com.be3c.sysmetic.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "inquiry")
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private InquiryAnswer inquiryAnswer;

    // 연관관계 편의 메서드
    public void setInquiryAnswer (InquiryAnswer inquiryAnswer) {
        this.inquiryAnswer = inquiryAnswer;
        inquiryAnswer.setInquiry(this);

//        if (this.inquiryAnswer != null) {
//            this.inquiryAnswer.setInquiry(null);
//        }
//
//        this.inquiryAnswer = inquiryAnswer;
//        if (inquiryAnswer != null) {
//            inquiryAnswer.setInquiry(this);
//        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // enum (ALL, UNCLOSED, CLOSED)
    @Enumerated(EnumType.STRING)
//    @Column(name = "status_code", length = 10, nullable = false)
    private InquiryStatus inquiryStatus;

    @Column(name = "inquiry_title", length = 100, nullable = false)
    private String inquiryTitle;

    @Column(name = "inquiry_content", length = 1000, nullable = false)
    private String inquiryContent;

    @Column(name = "inquiry_registration_date", columnDefinition = "Timestamp", nullable = false)
    private LocalDateTime inquiryRegistrationDate;

//    @Column(name = "created_by", nullable = false)
//    private Long createdBy;
//
//    @Column(name = "created_date", nullable = false, columnDefinition = "Timestamp default now()")
//    private LocalDateTime createdDate;
//
//    @Column(name = "modified_by", nullable = false)
//    private Long modifiedBy;
//
//    @Column(name = "modified_date", nullable = false,  columnDefinition = "Timestamp default now() on update now()")
//    private LocalDateTime modifiedDate;

    //==생성 메서드==//
//    public static Inquiry createInquiry(InquiryAnswer inquiryAnswer, Strategy strategy, Member member, String inquiryTitle, String inquiryContent) {
    public static Inquiry createInquiry(Strategy strategy, Member member, String inquiryTitle, String inquiryContent) {
        Inquiry inquiry = new Inquiry();
//        inquiry.setInquiryAnswer(inquiryAnswer);
        inquiry.setStrategy(strategy);
        inquiry.setMember(member);

        inquiry.setInquiryStatus(InquiryStatus.UNCLOSED);
        inquiry.setInquiryTitle(inquiryTitle);
        inquiry.setInquiryContent(inquiryContent);
        inquiry.setInquiryRegistrationDate(LocalDateTime.now());
        
        return inquiry;
    }

}
