package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "문의 등록 페이지 조회 요청 DTO")
public class InquirySavePageShowRequestDto {

    @Schema(description = "전략 ID", example = "12345")
    private Long strategyId;
}