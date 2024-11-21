package com.be3c.sysmetic.domain.member.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InquiryShowResponseDto {

    @Schema(description = "문의 ID", example = "1001")
    private Long inquiryId;

    @Schema(description = "문의 제목", example = "트레이더 문의")
    private String inquiryTitle;

    // 전략 위 아이콘들

    @Schema(description = "전략 이름", example = "Strategy A")
    private String strategyName;

    @Schema(description = "문의 등록 일자", example = "2024-11-22T12:30:00")
    private LocalDateTime inquiryRegistrationDate;

    @Schema(description = "문의 상태 (ALL, CLOSED, UNCLOSED)", example = "CLOSED")
    private InquiryStatus inquiryStatus;
}
