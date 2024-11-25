package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 수정 요청 DTO")
public class NoticeModifyRequestDto {

    @Schema(description = "공지사항 수정 화면에 들어온 시간", example = "2024-11-23 21:16:19.274999")
    private LocalDateTime modifyInModifyPageTime;

    @Schema(description = "공지사항 제목", example = "새로운 공지사항 제목")
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 내용 예시입니다.")
    private String noticeContent;

    @Schema(description = "수정자 ID", example = "admin123")
    private String correctorId;

    @Schema(description = "첨부 파일 여부 (0: 없음, 1: 있음)", example = "1")
    private Integer isAttatchment;

    @Schema(description = "공개 여부 (0: 비공개, 1: 공개)", example = "1")
    private Integer isOpen;
}
