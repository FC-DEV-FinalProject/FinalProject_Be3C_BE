package com.be3c.sysmetic.global.util.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 메인 페이지 전략 수 반환 dto")
public class AdminInquiryResponseDto {

    @Schema(description = "대기 중인 문의 개수")
    Long waitingInquiryCount;

    @Schema(description = "답변 완료된 문의 개수")
    Long answeredInquiryCount;

    @Schema(description = "총 문의 개수")
    Long inquiryCount;
}
