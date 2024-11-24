package com.be3c.sysmetic.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "폴더 이동 요청 DTO")
public class FollowPutRequestDto {

    @Schema(description = "원래 폴더의 ID", example = "1")
    @NotBlank
    private Long originFolderId;

    @Schema(description = "이동할 폴더의 ID", example = "2")
    @NotBlank
    private Long toFolderId;

    @Schema(description = "이동할 전략의 ID", example = "10")
    @NotBlank
    private Long strategyId;
}
