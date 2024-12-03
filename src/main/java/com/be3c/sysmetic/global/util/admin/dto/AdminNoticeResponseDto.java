package com.be3c.sysmetic.global.util.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "관리자 메인 페이지 전략 수 반환 dto")
public class AdminNoticeResponseDto {

    @Schema(description = "공지사항 ID")
    Long noticeId;

    @Schema(description = "공지사항 제목")
    String noticeTitle;

    @Schema(description = "공지사항 등록 날짜")
    LocalDate noticeRegistrationDate;

    public AdminNoticeResponseDto(Long noticeId, String noticeTitle, LocalDateTime noticeRegistrationDate) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeRegistrationDate = noticeRegistrationDate.toLocalDate();
    }
}
