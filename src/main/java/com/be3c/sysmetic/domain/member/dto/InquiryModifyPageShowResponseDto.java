package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 수정 페이지 조회 응답 DTO")
public class InquiryModifyPageShowResponseDto {

    @Schema(description = "문의 제목", example = "문의드립니다.")
    private String inquiryTitle;

    @Schema(description = "문의 내용", example = "무엇인가요?")
    private String inquiryContent;
}
