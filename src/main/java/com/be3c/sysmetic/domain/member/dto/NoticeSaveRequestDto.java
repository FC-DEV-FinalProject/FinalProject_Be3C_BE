package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 등록 요청 DTO")
public class NoticeSaveRequestDto {

    @Schema(description = "작성자 ID", example = "1234")
    private Long writerId;

    @Schema(description = "공지사항 제목", example = "공지사항 제목 예시")
    @NotNull
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 내용 예시입니다.")
    @NotNull
    private String noticeContent;

    @Schema(description = "첨부 파일 여부 (0: 없음, 1: 있음)", example = "1")
    @NotNull
    private Integer isAttatchment;

    @Schema(description = "공개 여부 (0: 비공개, 1: 공개)", example = "1")
    @NotNull
    private Integer isOpen;
}
