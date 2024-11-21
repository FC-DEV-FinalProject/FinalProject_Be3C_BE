package com.be3c.sysmetic.domain.member.dto;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 문의 목록 응답 DTO")
public class InquiryAdminShowRequestDto {

    @Schema(description = "답변 상태 탭 (ALL, CLOSED, UNCLOSED)", example = "ALL")
    private InquiryStatus tab; // ALL, CLOSED, UNCLOSED

    @Schema(description = "검색 유형 (전략명, 트레이더, 질문자)", example = "strategy")
    private String searchType; // 전략명, 트레이더, 질문자

    @Schema(description = "검색 텍스트", example = "트레이더1")
    private String searchText;
}