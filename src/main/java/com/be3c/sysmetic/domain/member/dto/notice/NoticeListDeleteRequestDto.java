package com.be3c.sysmetic.domain.member.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 삭제 요청 DTO")
public class NoticeListDeleteRequestDto {

    @Schema(description = "삭제할 공지사항 ID 리스트", example = "[1, 2, 3]")
    private List<Long> noticeIds;
}


