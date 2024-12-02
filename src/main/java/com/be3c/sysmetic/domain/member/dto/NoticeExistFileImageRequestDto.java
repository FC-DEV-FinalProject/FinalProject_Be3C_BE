package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 파일, 이미지 id와 존재유무 요청 DTO")
public class NoticeExistFileImageRequestDto {

    @Schema(description = "공지사항 파일, 이미지의 id", example = "1")
    private Long fileId;

    @Schema(description = "공지사항 파일, 이미지의 존재 유무", example = "true")
    private Boolean exist;
}
