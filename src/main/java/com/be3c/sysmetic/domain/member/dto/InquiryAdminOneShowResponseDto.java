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
@Schema(description = "관리자 문의 상세 조회 응답 DTO")
public class InquiryAdminOneShowResponseDto {

    @Schema(description = "문의 ID", example = "12345")
    private Long inquiryId;

    @Schema(description = "트레이더 닉네임", example = "TraderNick")
    private String traderNickname;

    // 전략 위 아이콘들

    @Schema(description = "전략 이름", example = "Strategy A")
    private String strategyName;

    @Schema(description = "문의 등록 일시", example = "2024-11-22T15:30:00")
    private LocalDateTime inquiryRegistrationDate;

    @Schema(description = "질문자 닉네임", example = "InquirerNick")
    private String inquirerNickname;

    @Schema(description = "문의 상태", example = "CLOSED")
    private InquiryStatus inquiryStatus;
}