package com.be3c.sysmetic.domain.member.dto.inquiry;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowAdminInquiryResponseDto {

    private Long id;
    private Long inquiryAnswerId;
    private Long strategyId;
    private Long traderId;
    private Long memberId;
    private String inquiryContent;


    private String strategyName;
    private String inquiryTitle;
    private String traderName;
    private LocalDateTime inquiryRegistrationDate;
    private InquiryStatus inquiryStatus;

}
