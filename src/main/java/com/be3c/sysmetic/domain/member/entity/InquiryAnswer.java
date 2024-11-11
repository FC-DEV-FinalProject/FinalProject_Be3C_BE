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
@Table(name = "inquiry_answer")
public class InquiryAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", unique = true)
    private Inquiry inquiry;

    @Column(name = "answer_content", length = 1000)
    private String answerContent;

    @Column(name = "answer_registration_date", columnDefinition = "Timestamp")
    private LocalDateTime answerRegistrationDate;

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
    public static InquiryAnswer createInquiryAnswer(Inquiry inquiry, String answerContent) {
//    public static InquiryAnswer createInquiryAnswer(String answerContent) {
        InquiryAnswer inquiryAnswer = new InquiryAnswer();
        inquiryAnswer.setInquiry(inquiry);
        inquiryAnswer.setAnswerContent(answerContent);
        inquiryAnswer.setAnswerRegistrationDate(LocalDateTime.now());
        inquiry.setInquiryAnswer(inquiryAnswer);

        return inquiryAnswer;
    }
}
