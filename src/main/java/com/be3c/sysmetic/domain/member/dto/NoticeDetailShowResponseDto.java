package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 조회 응답 DTO")
public class NoticeDetailShowResponseDto {

    @Schema(description = "공지사항 ID", example = "123")
    private Long noticeId;

    @Schema(description = "공지사항 제목", example = "공지사항 제목 예시")
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 내용 예시입니다.")
    private String noticeContent;

    @Schema(description = "작성일시", example = "2023-11-21T10:15:30")
    private LocalDateTime writeDate;

    @Schema(description = "수정일시", example = "2023-11-22T14:00:00")
    private LocalDateTime correctDate;

    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String writerNickname;

    @Schema(description = "조회수", example = "100")
    private Long hits;

    @Schema(description = "첨부 파일 여부 (0: 없음, 1: 있음)", example = "1")
    private Integer isAttatchment;

    @Schema(description = "이전글 제목", example = "공지사항 제목 예")
    private String previousTitle;

    @Schema(description = "이전글 작성일시", example = "2023-11-21T10:15:30")
    private LocalDateTime previousWriteDate;

    @Schema(description = "다음글 제목", example = "공지사항 제목 예")
    private String nextTitle;

    @Schema(description = "다음글 작성일시", example = "2023-11-21T10:15:30")
    private LocalDateTime nextWriteDate;
}