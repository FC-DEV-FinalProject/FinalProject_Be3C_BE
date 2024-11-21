package com.be3c.sysmetic.domain.member.dto.inquiry;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SaveInquiryRequestDto {

    private Long memberId;
    private Long strategyId;
    private String inquiryTitle;
    private String inquiryContent;
}
