package com.be3c.sysmetic.domain.member.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetailSaveRequestDto {

    @Schema(description = "답변 제목", example = "Strategy Inquiry Answer")
    private String answerTitle;

    @Schema(description = "답변 내용", example = "Here is the detailed answer to your inquiry.")
    private String answerContent;
}