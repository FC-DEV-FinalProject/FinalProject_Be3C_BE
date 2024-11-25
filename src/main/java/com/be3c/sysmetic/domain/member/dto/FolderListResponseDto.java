package com.be3c.sysmetic.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 목록 응답 DTO")
public class FolderListResponseDto {

    @Schema(description = "폴더 ID", example = "1")
    private Long id;

    @Schema(description = "폴더 이름", example = "관심 전략 폴더")
    private String name;

    @Schema(description = "가장 최근에 관심 전략이 추가된 날짜", example = "2024-11-20T12:34:56")
    private LocalDateTime latestInterestStrategyAddedDate;
}
