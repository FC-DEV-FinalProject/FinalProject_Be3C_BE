package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관리자 공지사항 목록 조회 응답 DTO")
public class NoticeAdminListShowResponseDto {

    @Schema(description = "관리자 공지사항 목록", example = "[]")
    List<NoticeAdminOneShowResponseDto> noticeAdminList;

    @Schema(description = "목록 크기", example = "100")
    Integer listSize;

    @Schema(description = "총 페이지", example = "10")
    Integer totalPage;

    @Schema(description = "총 요소 개수", example = "100")
    Long totalElements;

    @Schema(description = "첫 페이지인지", example = "true")
    Boolean isFirst;

    @Schema(description = "마지막 페이지인지", example = "true")
    Boolean isLast;
}
