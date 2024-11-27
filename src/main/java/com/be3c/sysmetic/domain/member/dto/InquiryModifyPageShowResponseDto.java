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

    @Schema(description = "이 공지사항이 있던 페이지", example = "1")
    private int page;

    @Schema(description = "지정했던 정렬순", example = "searchType")
    private String sort;

    @Schema(description = "지정했던 답변상태", example = "searchType")
    private String closed;

    @Schema(description = "문의 제목", example = "문의드립니다.")
    private String inquiryTitle;

    @Schema(description = "문의 내용", example = "무엇인가요?")
    private String inquiryContent;
}
