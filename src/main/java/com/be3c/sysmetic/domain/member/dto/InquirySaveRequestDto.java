package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquirySaveRequestDto {

    @Schema(description = "회원 ID", example = "12345")
    private Long memberId;

    @Schema(description = "전략 ID", example = "9876")
    private Long strategyId;

    @Schema(description = "문의 제목", example = "전략에 대한 문의")
    private String inquiryTitle;

    @Schema(description = "문의 내용", example = "이 전략에 대한 설명을 더 듣고 싶습니다.")
    private String inquiryContent;
}
