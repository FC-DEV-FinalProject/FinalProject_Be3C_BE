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
@Schema(description = "공지사항 수정 페이지 조회 응답 DTO")
public class NoticeShowModifyPageResponseDto {

    @Schema(description = "이 공지사항이 있던 페이지", example = "1")
    private int page;

    @Schema(description = "검색했던 검색 조건", example = "searchType")
    private String searchType;

    @Schema(description = "검색했던 검색 단어", example = "searchText")
    private String searchText;

    @Schema(description = "공지사항 ID", example = "123")
    private Long noticeId;

    @Schema(description = "공지사항 제목", example = "새로운 공지사항 제목")
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 내용 예시입니다.")
    private String noticeContent;

    @Schema(description = "첨부 파일 여부", example = "true")
    private Boolean isAttachment;

    @Schema(description = "공개 여부", example = "true")
    private Boolean isOpen;

    @Schema(description = "이 공지사항의 파일 정보를 담은 리스트", example = "[]")
    private List<NoticeDetailFileShowResponseDto> fileDtoList;

    @Schema(description = "이 공지사항의 이미지 정보를 담은 리스트", example = "[]")
    private List<NoticeDetailImageShowResponseDto> imageDtoList;
}
