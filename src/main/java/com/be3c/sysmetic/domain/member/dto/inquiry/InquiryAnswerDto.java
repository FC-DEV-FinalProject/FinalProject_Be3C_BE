package com.be3c.sysmetic.domain.member.dto.inquiry;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAnswerDto {

    private Long id;
    private Long inquiryId;

    private String strategyName;
    private String inquiryTitle;
    private String inquiryContent;
    private String memberName;
    private LocalDateTime inquiryRegistrationDate;

    private String answerContent;
    private String traderName;
    private LocalDateTime answerRegistrationDate;

}
