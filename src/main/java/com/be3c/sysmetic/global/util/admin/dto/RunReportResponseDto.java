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
@Schema(description = "관리자 구글 애널리틱스 반환 dto")
public class RunReportResponseDto {
    @Schema(description = "활성 사용자 수")
    private String activeUser;

    @Schema(description = "평균 세션 지속 시간")
    private String avgSessionDuration;
}
