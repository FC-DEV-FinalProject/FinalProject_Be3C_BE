package com.be3c.sysmetic.domain.member.dto.inquiry;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ModifyInquiryRequestDto {

    private Long inquiryId;
    private String inquiryTitle;
    private String inquiryContent;
}
