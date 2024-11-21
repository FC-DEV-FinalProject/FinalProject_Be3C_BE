package com.be3c.sysmetic.domain.member.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 목록 조회 응답 DTO")
public class ShowNoticeResponseDto {

    @Schema(description = "공지사항 ID", example = "123")
    private Long noticeId;

    @Schema(description = "공지사항 제목", example = "공지사항 제목 예시")
    private String noticeTitle;

    @Schema(description = "작성일시", example = "2023-11-21T10:15:30")
    private LocalDateTime writeDate;

    @Schema(description = "첨부 파일 여부 (0: 없음, 1: 있음)", example = "1")
    private Integer isAttatchment;
}