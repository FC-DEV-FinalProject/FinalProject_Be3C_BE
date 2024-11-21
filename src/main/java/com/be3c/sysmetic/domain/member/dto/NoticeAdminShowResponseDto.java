package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 공지사항 목록 조회 응답 DTO")
public class NoticeAdminShowResponseDto {

    @Schema(description = "공지사항 ID", example = "123")
    private Long noticeId;

    @Schema(description = "공지사항 제목", example = "공지사항 제목 예시")
    private String noticeTitle;

    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String writerNickname;

    @Schema(description = "작성일시", example = "2023-11-21T10:15:30")
    private LocalDateTime writeDate;

    @Schema(description = "조회수", example = "100")
    private Long hits;

    @Schema(description = "첨부 파일 여부 (0: 없음, 1: 있음)", example = "1")
    private Integer isAttatchment;

    @Schema(description = "공개 여부 (0: 비공개, 1: 공개)", example = "1")
    private Integer isOpen;
}
