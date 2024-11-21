package com.be3c.sysmetic.domain.member.dto;

import com.be3c.sysmetic.domain.member.entity.InquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 문의 답변 조회 응답 DTO")
public class InquiryAnswerShowResponseDto {

    @Schema(description = "문의 ID", example = "12345")
    private Long inquiryId;

    @Schema(description = "문의 답변 ID", example = "54321")
    private Long inquiryAnswerId;

    @Schema(description = "문의 제목", example = "Strategy Inquiry")
    private String inquiryTitle;

    @Schema(description = "문의 등록 일시", example = "2024-11-22T15:30:00")
    private LocalDateTime inquiryRegistrationDate;

    @Schema(description = "질문자 닉네임", example = "InquirerNick")
    private String inquirerNickname;

    @Schema(description = "문의 상태", example = "CLOSED")
    private InquiryStatus inquiryStatus;

    // 전략 위 아이콘들

    @Schema(description = "전략 이름", example = "Strategy A")
    private String strategyName;

    // 트레이더의 아이콘

    @Schema(description = "트레이더 닉네임", example = "TraderNick")
    private String traderNickname;

    @Schema(description = "문의 내용", example = "What is the strategy?")
    private String inquiryContent;

    @Schema(description = "답변 제목", example = "Strategy Details")
    private String answerTitle;

    @Schema(description = "답변 등록 일시", example = "2024-11-22T16:00:00")
    private LocalDateTime answerRegistrationDate;

    @Schema(description = "답변 내용", example = "Here are the details of the strategy.")
    private String answerContent;
}