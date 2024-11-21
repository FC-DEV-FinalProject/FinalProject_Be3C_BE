package com.be3c.sysmetic.domain.member.dto.inquiry;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquiryShowRequestDto {

    // 일반회원 별
    private Long memberId;

    // 트레이더 별
    private Long traderId;

    // 답변상태 탭
    private InquiryStatus tab; // ALL, CLOSED, UNCLOSED

    // 검색
    private String searchCondition; // 전략명, 트레이더, 질문자
    private String searchKeyword;

//    private Long page;
}
