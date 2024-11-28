package com.be3c.sysmetic.global.util.admin.dto;

import com.google.analytics.data.v1beta.RunReportResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 메인 페이지 유저 비율 반환 dto")
public class RunReportResponseDto {
    RunReportResponse dayReportResponse;
    RunReportResponse weeklyReportResponse;
    RunReportResponse monthlyReportResponse;
    RunReportResponse allReportResponse;
}
