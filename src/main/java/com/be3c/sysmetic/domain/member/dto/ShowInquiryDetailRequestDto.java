package com.be3c.sysmetic.domain.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowInquiryDetailRequestDto {

    private String tab; // all, closed, unclosed

    private Long page;

    private Integer inquiryId;
}
