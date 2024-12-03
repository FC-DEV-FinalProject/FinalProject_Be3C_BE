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
    String activeUser;
    String avgSessionDuration;
}
