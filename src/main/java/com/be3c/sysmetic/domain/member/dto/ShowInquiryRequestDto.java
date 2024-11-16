package com.be3c.sysmetic.domain.member.dto;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowInquiryRequestDto {

    private Long memberId;

    private Long traderId;

    private InquiryStatus tab; // ALL, CLOSED, UNCLOSED

    private String searchCondition; // 전략명, 트레이더, 질문자
    private String searchKeyword;

//    private Long page;
}
