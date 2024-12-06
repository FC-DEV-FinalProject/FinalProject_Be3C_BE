package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "관심 전략 조회 요청 DTO")
public class InterestStrategyGetRequestDto {

    @NotNull
    @Schema(description = "폴더 ID", example = "1")
    private Long folderId;

    @NotNull
    @Schema(description = "페이지 번호", example = "0")
    private Integer page;
}
