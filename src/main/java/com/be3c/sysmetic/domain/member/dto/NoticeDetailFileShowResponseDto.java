package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 조회 파일 응답 DTO")
public class NoticeDetailFileShowResponseDto {

    @Schema(description = "공지사항 파일의 id", example = "1")
    private Long fileId;

    @Schema(description = "공지사항 파일의 크기", example = "100")
    private long fileSize;

    @Schema(description = "공지사항 파일의 이름", example = "image")
    private String originalName;

    @Schema(description = "공지사항 파일의 파일 경로", example = "/file")
    private String path;
}
