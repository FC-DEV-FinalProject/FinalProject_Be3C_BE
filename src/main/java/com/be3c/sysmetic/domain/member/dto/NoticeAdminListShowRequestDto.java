package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 공지사항 조회 요청 DTO")
public class NoticeAdminListShowRequestDto {

    // 공개 여부별
    @Schema(description = "공개 여부 (0: 비공개, 1: 공개)", example = "1")
    private Integer isOpen;

    // 검색
    @Schema(description = "검색 유형 (제목, 내용, 제목+내용, 작성자)", example = "title")
    private String searchType;

    @Schema(description = "검색 텍스트", example = "트레이더1")
    private String searchText;
}
