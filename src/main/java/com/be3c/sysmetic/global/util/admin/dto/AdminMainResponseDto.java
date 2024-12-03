package com.be3c.sysmetic.global.util.admin.dto;


import com.be3c.sysmetic.global.common.response.PageResponse;
import com.google.analytics.data.v1beta.RunReportResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 메인 페이지 반환 dto")
public class AdminMainResponseDto {
    @Schema(description = "보고서 응답 DTO")
    RunReportResponseDto runReportResponseDto;

    @Schema(description = "회원 수 정보 DTO")
    MemberCountResponseDto memberCount;

    @Schema(description = "전략 수 정보 DTO")
    StrategyCountResponseDto strategyCount;

    @Schema(description = "문의 수 정보 DTO")
    AdminInquiryResponseDto adminInquiryResponseDto;

    @Schema(description = "관리자 공지 리스트")
    List<AdminNoticeResponseDto> adminNoticeResponse;
}
