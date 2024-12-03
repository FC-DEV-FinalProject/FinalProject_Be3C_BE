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
    RunReportResponseDto runReportResponseDto;
    MemberCountResponseDto memberCount;
    StrategyCountResponseDto strategyCount;
    AdminInquiryResponseDto adminInquiryResponseDto;
    List<AdminNoticeResponseDto> adminNoticeResponse;
}
