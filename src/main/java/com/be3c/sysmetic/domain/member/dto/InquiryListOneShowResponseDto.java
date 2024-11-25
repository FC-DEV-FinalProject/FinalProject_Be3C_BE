package com.be3c.sysmetic.domain.member.dto;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 상세 조회 응답 DTO")
public class InquiryListOneShowResponseDto {

    @Schema(description = "문의 ID", example = "12345")
    private Long inquiryId;

    @Schema(description = "문의 제목", example = "문의드립니다.")
    private String inquiryTitle;

    // 전략 위 아이콘들

    @Schema(description = "전략 이름", example = "Strategy A")
    private String strategyName;

    @Schema(description = "문의 등록 일시", example = "2024-11-22T15:30:00")
    private LocalDateTime inquiryRegistrationDate;

    @Schema(description = "문의 상태", example = "closed")
    private InquiryStatus inquiryStatus;

}
